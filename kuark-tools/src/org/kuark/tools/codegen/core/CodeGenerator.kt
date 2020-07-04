package org.kuark.tools.codegen.core

import freemarker.cache.MultiTemplateLoader
import freemarker.cache.URLTemplateLoader
import freemarker.template.Configuration
import javafx.scene.control.Alert
import org.kuark.base.io.FileKit
import org.kuark.base.log.LogFactory
import org.kuark.tools.codegen.core.merge.CodeMerger
import org.kuark.tools.codegen.core.merge.PrivateContentEraser
import org.kuark.tools.codegen.service.CodeGenColumnService
import org.kuark.tools.codegen.service.CodeGenFileService
import org.kuark.tools.codegen.service.CodeGenObjectService
import org.kuark.tools.codegen.vo.GenFile
import java.io.File
import java.net.URL

/**
 * 代码生成器，代码生成核心逻辑处理
 *
 * @author K
 * @since 1.0.0
 */
class CodeGenerator(
    private val templateModel: Map<String, Any?>,
    private val genFiles: List<GenFile>
) {

    fun generate() {
        genFiles.forEach { executeGenerate(it) }
        val persistence = persistence()
        if (persistence) {
            Alert(
                Alert.AlertType.INFORMATION, "生成成功，请查看目录：${CodeGeneratorContext.config.getCodeLoaction()}".trimIndent()
            ).show()
        } else {
            Alert(Alert.AlertType.INFORMATION, "生成成功，但配置信息持久化失败! ").show()
        }
    }

    private fun persistence(): Boolean {
        var success = CodeGenObjectService.saveOrUpdate()
        if (success) {
            success = CodeGenColumnService.saveColumns()
            if (success) {
                val filenames = genFiles.filter { it.getGenerate() }.map { it.getFilename() }
                success = CodeGenFileService.save(filenames)
            }
        }
        return success
    }

    private fun newFreeMarkerConfiguration(): Configuration {
        val templateRootDir = CodeGeneratorContext.config.getTemplateInfo()!!.rootDir
        val multiTemplateLoader = MultiTemplateLoader(arrayOf(MyURLTemplateLoader(URL("file:$templateRootDir"))))
        val conf = Configuration()
        conf.templateLoader = multiTemplateLoader
        conf.numberFormat = "###############"
        conf.booleanFormat = "true,false"
        conf.defaultEncoding = "UTF-8"
        conf.setClassForTemplateLoading(
            CodeGenerator::class.java, "/template/${CodeGeneratorContext.config.getTemplateInfo()!!.name}"
        )
        val autoIncludes = listOf("macro.include")
        val availableAutoInclude = FreemarkerKit.getAvailableAutoInclude(conf, autoIncludes)
        conf.setAutoIncludes(availableAutoInclude)
        log.debug("set Freemarker.autoIncludes:$availableAutoInclude for templateName:$templateRootDir autoIncludes:$autoIncludes")
        return conf
    }

    private fun executeGenerate(genFile: GenFile) {
        val template = newFreeMarkerConfiguration().getTemplate(genFile.templateFileRelativePath)
        template.outputEncoding = "UTF-8"
        val absoluteOutputFilePath =
            File("${CodeGeneratorContext.config.getCodeLoaction()}/${genFile.finalFileRelativePath}")
        val exists = absoluteOutputFilePath.exists()
        val existStr = if (exists) "Override " else ""
        log.debug("[" + existStr + "generate]\t template:${genFile.getDirectory()}/${genFile.getFilename()} ==> ${genFile.finalFileRelativePath}")
        var codeMerger: CodeMerger? = null
        if (exists) {
            codeMerger = CodeMerger(absoluteOutputFilePath)
        } else {
            FileKit.touch(absoluteOutputFilePath)
        }
        FreemarkerKit.processTemplate(template, templateModel, absoluteOutputFilePath, "UTF-8")
        if (codeMerger != null) {
            codeMerger.merge()
        } else {
            PrivateContentEraser.erase(absoluteOutputFilePath)
        }
    }

    private class MyURLTemplateLoader(private val root: URL) : URLTemplateLoader() {
        override fun getURL(template: String): URL {
            return URL(root, template)
        }
    }

    companion object {
        private val log = LogFactory.getLog(CodeGenerator::class)
    }
}