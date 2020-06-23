package org.kuark.tools.codegen.fx.ui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import org.kuark.tools.codegen.fx.controller.ColumnsController
import org.kuark.tools.codegen.fx.controller.ConfigController
import org.kuark.tools.codegen.fx.controller.FilesController
import org.kuark.tools.fx.controls.wizard.LinearWizardFlow
import org.kuark.tools.fx.controls.wizard.Wizard

/**
 * Create by (admin) on 2015/5/26.
 */
class CodeGenerateWizard : Application() {

    override fun start(stage: Stage) {
        val wizard = Wizard()

//        wizard.setTitle("代码生成器");

        // config page
        var fxmlLoader = FXMLLoader()
        val databasePanel = fxmlLoader.load<Parent>(javaClass.getResourceAsStream("/fxml/config.fxml"))
        val configController = fxmlLoader.getController<ConfigController>()

        // --- page 2
        fxmlLoader = FXMLLoader()
        val columnsPanel = fxmlLoader.load<Parent>(javaClass.getResourceAsStream("/fxml/columns.fxml"))
        val columnsController = fxmlLoader.getController<ColumnsController>()

        // --- page 1
        val page1 = object : Wizard.WizardPane() {
            override fun onExitingPage(wizard: Wizard?) { //wizard的bug: 从page3回到page2会执行该方法
                try {
                    configController.canGoOn()
                    configController.storeConfig()
                    val conf = configController.config
                    //TODO
//                    DataContext.setDataSource(DbUtils.createDataSource(conf!!.dbUrl, conf.dbUser, conf.dbPassword))
                    columnsController.setConfig(conf)
                } catch (e: Exception) {
                    Alert(Alert.AlertType.ERROR, e.message).show()
                }
            }
        }
        page1.headerText = "请配置以下信息："
        page1.content = databasePanel

        // --- page 3
        fxmlLoader = FXMLLoader()
        val filesPanel = fxmlLoader.load<Parent>(javaClass.getResourceAsStream("/fxml/files.fxml"))
        val filesController = fxmlLoader.getController<FilesController>()
        val page3 = object : Wizard.WizardPane() {
            override fun onEnteringPage(wizard: Wizard?) {
                filesController.readFiles()
                val button = lookupButton(ButtonType.FINISH) as Button
                button.isDisable = true
            }
        }
        page3.headerText = "请选择要生成的文件："
        page3.content = filesPanel
        val page2 = object : Wizard.WizardPane() {
            override fun onExitingPage(wizard: Wizard?) {
                val table = columnsController.table
                if (table == null) {
                    Alert(Alert.AlertType.ERROR, "请先选择表！").show()
                } else {
                    filesController.setTable(table)
                    filesController.setTableComment(columnsController.tableComment)
                    filesController.setColumns(columnsController.columns)
                    filesController.setConfig(columnsController.getConfig())
                }
            }

            override fun onEnteringPage(wizard: Wizard?) {
                println("entering page 2")
            }
        }
        page2.headerText = "请定制列信息："
        page2.content = columnsPanel


        // create wizard
        wizard.setFlow(LinearWizardFlow(page1, page2, page3))

        // show wizard and wait for response
        wizard.showAndWait().ifPresent { result: ButtonType? ->
            if (result == ButtonType.FINISH) {
                println("Wizard finished, settings: " + wizard.settings)
            } else if (result == ButtonType.PREVIOUS) {
                println("PREVIOUS")
            } else if (result == ButtonType.NEXT) {
                println("NEXT")
            }
        }
    }

//    private fun maxScreen(stage: Stage) {
//        val primaryScreenBounds = Screen.getPrimary().visualBounds
//        stage.x = primaryScreenBounds.minX
//        stage.y = primaryScreenBounds.minY
//        stage.width = primaryScreenBounds.width
//        stage.height = primaryScreenBounds.height
//    }

}