package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {

    val image = inputImage()
    val watermark = inputWatermark(image)
    val weight = inputWeight()
    val outputName = outputImageFileName()
    val extension = outputExtension(outputName)
    val transparency = checkForTransparency(watermark)
    val outputImage = outputImage(image, watermark, weight)
    createOutputFile(outputName, extension, outputImage)

}

fun inputImage(): BufferedImage {
    val image: BufferedImage
    print("Input the image filename:\n> ")
    val imageName = readln()
    val imageFile = File(imageName)

    if (!imageFile.exists() && !imageFile.isFile) {
        println("The file $imageName doesn't exist.")
        exitProcess(0)
    } else if (ImageIO.read(imageFile).colorModel.numComponents !in 3..4) {
        println("The number of image color components isn't 3 or 4.")
        exitProcess(0)
    } else if (ImageIO.read(imageFile).colorModel.pixelSize !in 24..32) {
        println("The image isn't 24 or 32-bit.")
        exitProcess(0)
    } else {
        image = ImageIO.read(imageFile)
    }
    return image
}
fun inputWatermark(image: BufferedImage): BufferedImage {
    val watermark: BufferedImage
    print("Input the watermark image filename:\n> ")
    val watermarkName = readln()
    val watermarkFile = File(watermarkName)

    if (!watermarkFile.exists() && !watermarkFile.isFile) {
        println("The file $watermarkName doesn't exist.")
        exitProcess(0)
    } else if (ImageIO.read(watermarkFile).colorModel.numComponents !in 3..4) {
        println("The number of watermark color components isn't 3 or 4.")
        exitProcess(0)
    } else if (ImageIO.read(watermarkFile).colorModel.pixelSize !in 24..32) {
        println("The watermark isn't 24 or 32-bit.")
        exitProcess(0)
    } else if ((image.height != ImageIO.read(watermarkFile).height) && (image.width != ImageIO.read(watermarkFile).width)) {
        println("The image and watermark dimensions are different.")
        exitProcess(0)
    } else {
        watermark = ImageIO.read(watermarkFile)
    }
    return watermark
}
fun inputWeight (): Int {
    val weight: Int

    print("Input the watermark transparency percentage (Integer 0-100):\n> ")
    val weightString = readln()

    for (char in weightString) {
        if (!char.isDigit()) {
            println("The transparency percentage isn't an integer number.")
            exitProcess(0)
        }
    }

    weight = weightString.toInt()

    if (weight !in 0..255) {
        println("The transparency percentage is out of range.")
        exitProcess(0)
    }
    return weight
}
fun outputImageFileName (): String {

    print("Input the output image filename (jpg or png extension):\n> ")
    val outputName = readln()

    val png = outputName.endsWith("png")
    val jpg = outputName.endsWith("jpg")

    if (!png && !jpg) {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(0)
    }
    return outputName
}
fun outputExtension(outputName: String): String {
    return if (outputName.endsWith("png")) "png" else "jpg"
}
fun checkForTransparency (watermark: BufferedImage): Boolean {
    return if (watermark.transparency == 3) {
        println("Do you want to use the watermark's Alpha channel?")
//        if (readln() == "yes") {
//            ...
//        }
        true
    } else false
}
fun outputImage (image: BufferedImage, watermark: BufferedImage, weight: Int): BufferedImage {
    val outputImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

    for (x in 0 until image.width) {
        for (y in 0 until image.height) {

            val i = Color(image.getRGB(x, y))
            val w = Color(watermark.getRGB(x, y))

            val color = Color(
                (weight * w.red + (100 - weight) * i.red) / 100,
                (weight * w.green + (100 - weight) * i.green) / 100,
                (weight * w.blue + (100 - weight) * i.blue) / 100
            )
            outputImage.setRGB(x, y, color.rgb)
        }
    }
    return outputImage
}
fun createOutputFile (outputName: String, extension: String, outputImage: BufferedImage): File {
    val outputFile = File(outputName)
    outputFile.createNewFile()
    ImageIO.write(outputImage, extension, outputFile)

    println("The watermarked image $outputName has been created.")
    return outputFile
}
