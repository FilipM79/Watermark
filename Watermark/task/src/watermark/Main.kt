package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {

    val image = inputImage()
    val watermark = inputWatermark(image)
    val useAlpha = useAlpha(watermark)
    val useFakeAlpha = useFakeAlpha(watermark)
    val fakeAlphaColor = fakeAlphaColor(useFakeAlpha, watermark)
    val weight = inputWeight()
    val outputName = makeOutputImageFileName()
    val extension = outputExtension(outputName)

    val outputImage = createOutputImage(image, watermark, weight, useAlpha, useFakeAlpha, fakeAlphaColor)
    createOutputFile(outputName, extension, outputImage)
}
fun inputImage(): BufferedImage {

    val image: BufferedImage

    print("Input the image filename:\n> ")
    val imageName = readln()

    val imageFile = File(imageName)
    val correctExtension =  (imageName.endsWith(".png") || imageName.endsWith(".jpg"))

    if (!correctExtension) {
        println("The file $imageName doesn't exist.")
        exitProcess(0)

    } else if (!imageFile.exists() && !imageFile.isFile) {

        println("The file $imageName doesn't exist.")
        exitProcess(0)

    } else if (ImageIO.read(imageFile).colorModel.numComponents != 3) {

        println("The number of image color components isn't 3.")
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

    lateinit var watermark: BufferedImage

    print("Input the watermark image filename:\n> ")
    val watermarkName = readln()

    val watermarkFile = File(watermarkName)
    val correctExtension =  (watermarkName.endsWith(".png") || watermarkName.endsWith(".jpg"))

    if (!correctExtension) {
        println("The file $watermarkName doesn't exist.")
        exitProcess(0)

    } else if (!watermarkFile.exists() && !watermarkFile.isFile) {

        println("The file $watermarkName doesn't exist.")
        exitProcess(0)

    } else if (ImageIO.read(watermarkFile).transparency != 3
        && ImageIO.read(watermarkFile).colorModel.numComponents < 3) {

        println("The number of watermark color components isn't 3.")
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
fun useAlpha (watermark: BufferedImage): Boolean {

    return if (watermark.transparency == 3) {
        print("Do you want to use the watermark's Alpha channel?\n> ")

        readln().lowercase() == "yes"

    } else false
}
fun useFakeAlpha (watermark: BufferedImage): Boolean {

    return if (watermark.transparency != 3) {
        println("Do you want to set a transparency color?")
        (readln() == "yes")

    } else {
        false
    }
}
fun fakeAlphaColor (useFakeAlpha: Boolean, watermark: BufferedImage): Color {

    var fakeAlpha = Color (0, 0, 0)

    if (watermark.transparency != 3 && useFakeAlpha) {
        print("Input a transparency color ([Red] [Green] [Blue]):\n> ")
        val inputString = readln()
        val colorValues = mutableListOf<Int>()

        if (inputString.isNotEmpty()) {
            val colorStrings = inputString.split(" ")
            var isDigit = true
            for (value in colorStrings) {
                for (i in value) {
                    if (!i.isDigit()) isDigit = false
                }
                if (isDigit && value.toInt() in 0..255) colorValues.add(value.toInt())
            }
        }

        if (colorValues.size != 3) {
            println("The transparency color input is invalid.")
            exitProcess(0)
        } else {
            fakeAlpha = Color(colorValues[0], colorValues[1], colorValues[2])
        }
    }
    return fakeAlpha
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

    if (weight !in 0..100) {
        println("The transparency percentage is out of range.")
        exitProcess(0)
    }
    return weight
}
fun makeOutputImageFileName (): String {

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
fun outputExtension (outputName: String): String {
    return if (outputName.endsWith("png")) "png" else "jpg"
}
fun createOutputImage (
    image:BufferedImage, watermark:BufferedImage, weight:Int, useAlpha:Boolean, useFakeAlpha:Boolean, fakeAlpha:Color
): BufferedImage {

    val outputImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

    for (x in 0 until image.width) {
        for (y in 0 until image.height) {

            if (useAlpha) {
                val w = Color(watermark.getRGB(x, y), true)

                if (w.alpha == 0) outputImage.setRGB(x, y, Color(image.getRGB(x, y)).rgb)
                if (w.alpha == 255) {
                    val i = Color(image.getRGB(x, y) )
                    val color = Color(
                        (weight * w.red + (100 - weight) * i.red) / 100,
                        (weight * w.green + (100 - weight) * i.green) / 100,
                        (weight * w.blue + (100 - weight) * i.blue) / 100
                    )
                    outputImage.setRGB(x, y, color.rgb)
                }

            } else if (useFakeAlpha) {
                val w = Color(watermark.getRGB(x, y), true)

                if (w.rgb == fakeAlpha.rgb) {
                    outputImage.setRGB(x, y, Color(image.getRGB(x, y)).rgb)
                } else {
                    val i = Color(image.getRGB(x, y) )
                    val color = Color(
                        (weight * w.red + (100 - weight) * i.red) / 100,
                        (weight * w.green + (100 - weight) * i.green) / 100,
                        (weight * w.blue + (100 - weight) * i.blue) / 100
                    )
                    outputImage.setRGB(x, y, color.rgb)
                }

            } else {
                val i = Color(image.getRGB(x, y) )
                val w = Color(watermark.getRGB(x, y))
                val color = Color(
                    (weight * w.red + (100 - weight) * i.red) / 100,
                    (weight * w.green + (100 - weight) * i.green) / 100,
                    (weight * w.blue + (100 - weight) * i.blue) / 100
                )
                outputImage.setRGB(x, y, color.rgb)
            }
        }
    }
    return outputImage
}
fun createOutputFile (outputName: String, extension: String, outputImage: BufferedImage): File {

    val outputFile = File(outputName)

    if (!outputFile.parent.isNullOrBlank() && !outputFile.parentFile.exists()) {
        val parent = outputFile.parentFile
        parent.mkdirs()
    }

    outputFile.createNewFile()
    ImageIO.write(outputImage, extension, outputFile)

    println("The watermarked image $outputName has been created.")
    return outputFile
}
