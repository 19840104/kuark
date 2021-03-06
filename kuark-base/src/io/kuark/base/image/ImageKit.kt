package io.kuark.base.image

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel


/**
 * 图片处理工具类
 *
 * @author K
 * @since 1.0.0
 */
object ImageKit {

    /**
     * 从文件读取图片
     *
     * @param imageFile 图片文件
     * @return BufferedImage对象
     * @author K
     * @since 1.0.0
     */
    fun readImageFromFile(imageFile: File): BufferedImage = readImageFromFile(imageFile.path)

    /**
     * 从文件读取图片
     *
     * @param imagePath 图片路径
     * @return BufferedImage对象
     * @author K
     * @since 1.0.0
     */
    fun readImageFromFile(imagePath: String): BufferedImage = ImageIO.read(Files.newInputStream(Paths.get(imagePath)))

    /**
     * 从URI读取文件
     *
     * @param imageUri 图片uri
     * @return BufferedImage对象
     * @author K
     * @since 1.0.0
     */
    fun readImageFromUri(imageUri: URI): BufferedImage = ImageIO.read(imageUri.toURL().openStream())

    /**
     * 从URI读取文件
     *
     * @param imageUriStr 图片网络地址
     * @return BufferedImage对象
     * @author K
     * @since 1.0.0
     */
    fun readImageFromUri(imageUriStr: String): BufferedImage = readImageFromUri(URI.create(imageUriStr))

    /**
     * 写图片到文件
     *
     * @param bufferedImage BufferedImage对象
     * @param imageFormat 图片类型(如png,jpg,gif等)
     * @param imagePath 图片目标文件路径
     * @author K
     * @since 1.0.0
     */
    fun writeImage(bufferedImage: BufferedImage, imageFormat: String, imagePath: String) =
        ImageIO.write(bufferedImage, imageFormat, Files.newOutputStream(Paths.get(imagePath)))

    /**
     * 将图片转换为字符串表示
     *
     * @param imageFile 图片文件
     * @param imageFormat 图片类型(如png,jpg,gif等)
     * @return 图片的字符串表示
     * @author K
     * @since 1.0.0
     */
    fun imageToString(imageFile: File, imageFormat: String): String {
        val bufferedImage = readImageFromFile(imageFile)
        return imageToString(bufferedImage, imageFormat)
    }

    /**
     * 将图片转换为字符串表示
     *
     * @param imageUri 图片uri
     * @param imageFormat 图片类型(如png,jpg,gif等)
     * @return 图片的字符串表示
     * @author K
     * @since 1.0.0
     */
    fun imageToString(imageUri: URI, imageFormat: String): String {
        val bufferedImage = readImageFromUri(imageUri)
        return imageToString(bufferedImage, imageFormat)
    }

    /**
     * 将图片转换为字符串表示
     *
     * @param bufferedImage BufferedImage对象
     * @param imageFormat 图片类型(如png,jpg,gif等)
     * @return 图片的字符串表示(Base64编码)
     * @author K
     * @since 1.0.0
     */
    fun imageToString(bufferedImage: BufferedImage, imageFormat: String): String {
        return ByteArrayOutputStream().use {
            ImageIO.write(bufferedImage, imageFormat, it)
            val byteArray = it.toByteArray()
            String(Base64.getEncoder().encode(byteArray))
        }
    }

    /**
     * 将字符串表示转换为图片对象
     *
     * @param imgStr 图片的字符串表示(Base64编码)
     * @return BufferedImage对象
     * @author K
     * @since 1.0.0
     */
    fun stringToImage(imgStr: String): BufferedImage {
        val decoder = Base64.getDecoder()
        val imageByte = decoder.decode(imgStr)
        return ByteArrayInputStream(imageByte).use {
            ImageIO.read(it)
        }
    }


    /**
     * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
     *
     * @param srcImageFile 源文件地址
     * @param height       目标高度
     * @param width        目标宽度
     * @param hasFiller    比例不对时是否需要补白：true为补白（缺省值）; false为不补白;
     * @author K
     * @since 1.0.0
     */
    fun scale(srcImageFile: String, height: Int, width: Int, hasFiller: Boolean = true): BufferedImage {
        var ratio = 0.0 // 缩放比例
        val file = File(srcImageFile)
        val srcImage: BufferedImage = ImageIO.read(file)
        var destImage = srcImage.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH)
        // 计算比例
        if (srcImage.height > height || srcImage.width > width) {
            ratio = if (srcImage.height > srcImage.width) {
                height.toDouble() / srcImage.height
            } else {
                width.toDouble() / srcImage.width
            }
            val op = AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null)
            destImage = op.filter(srcImage, null)
        }
        if (hasFiller) { // 补白
            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val graphic = image.createGraphics()
            graphic.color = Color.white
            graphic.fillRect(0, 0, width, height)
            if (width == destImage.getWidth(null)) graphic.drawImage(
                destImage, 0, (height - destImage.getHeight(null)) / 2, destImage.getWidth(null),
                destImage.getHeight(null), Color.white, null
            ) else graphic.drawImage(
                destImage, (width - destImage.getWidth(null)) / 2, 0, destImage.getWidth(null),
                destImage.getHeight(null), Color.white, null
            )
            graphic.dispose()
            destImage = image
        }
        return destImage as BufferedImage
    }

    /**
     * 图形化展现图片对象
     *
     * @param bufferedImage BufferedImage对象
     * @author K
     * @since 1.0.0
     */
    fun showImage(bufferedImage: BufferedImage) {
        class ImagePanel(var image: BufferedImage) : JPanel() {

            override fun paintComponent(g: Graphics) {
                val x: Int = (width - image.width) / 2
                val y: Int = (height - image.height) / 2
                g.drawImage(image, x, y, this)
            }

            override fun getPreferredSize(): Dimension {
                return Dimension(image.width, image.height)
            }
        }

        val panel = ImagePanel(bufferedImage)
        val f = JFrame()
        f.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        f.add(panel)
        f.setLocation(200, 200)
        f.isVisible = true
        f.pack()
    }


}