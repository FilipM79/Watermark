package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {

    lateinit var image: BufferedImage
    lateinit var watermark: BufferedImage
    var weight = 0
    var outputName = ""
    var extension = ""

    loop@ while (true) {

//        print("Input the image filename:\n> ")
        val fileName = "image.png"
//        val fileName = readln()
        val imageFile = File(fileName)
        if (!imageFile.exists() && !imageFile.isFile) {
            println("The file $fileName doesn't exist.")
            break@loop
        } else if (ImageIO.read(imageFile).colorModel.pixelSize !in 24..32) {
            println("The image isn't 24 or 32 bit.")
            break@loop
        } else if (ImageIO.read(imageFile).colorModel.numComponents != 3) {
            println("The number of image color components isn't 3.")
            break@loop
        }
        image = ImageIO.read(imageFile)

//        print("Input the watermark image filename:\n> ")
        val watermarkInput = "watermark.png"
//        val watermarkInput = readln()
        val watermarkFile = File(watermarkInput)
        watermark = if (!watermarkFile.exists() && !watermarkFile.isFile) {
            println("The file $fileName doesn't exist.")
            break@loop
        } else if (ImageIO.read(imageFile).colorModel.pixelSize !in 24..32) {
            println("The image isn't 24 or 32 bit.")
            break@loop
        } else if ((image.height != ImageIO.read(imageFile).height) && (image.width != ImageIO.read(imageFile).width)) {
            println("The image and watermark dimensions are different.")
            break@loop
        } else {
            ImageIO.read(imageFile)
        }

//        print("Input the watermark transparency percentage (Integer 0-100):\n> ")
        val weightString = "50"
//        val weightString = readln()
        for (char in weightString) {
            if (!char.isDigit()) {
                println("The transparency percentage isn't an integer number.")
                break@loop
            }
        }
        weight = weightString.toInt()
        if (weight !in 0..100) {
            println("The transparency percentage is out of range.")
            break@loop
        }

//        print("Input the output image filename (jpg or png extension):\n> ")
        outputName = "test.jpg"
//        outputName = readln()
        val png = outputName.endsWith("png")
        val jpg = outputName.endsWith("jpg")

        if (!png && !jpg) {
            println("The output file extension isn't \"jpg\" or \"png\".")
            break@loop
        }
        extension = if (png) ".png" else ".jpg"

        break@loop
    }

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

    val outputFile = File(outputName)
    outputFile.resolve(outputName)
    outputFile.createNewFile()
    ImageIO.write(outputImage, extension, outputFile)

    println(outputFile.absolutePath)
    println(outputName)
    println(outputFile.isFile)
}



//    if (imageFile.isFile) {
//        val image: BufferedImage = ImageIO.read(imageFile)
//        var transparency = ""
//
//        when (image.colorModel.transparency) {
//            1 -> transparency = "OPAQUE"
//            2 -> transparency = "BITMASK"
//            3 -> transparency = "TRANSLUCENT"
//        }
//
//        println("Image file: $imageFile")
//        println("Width: ${image.width}")
//        println("Height: ${image.height}")
//        println("Number of components: ${image.colorModel.numComponents}")
//        println("Number of color components: ${image.colorModel.numColorComponents}")
//        println("Bits per pixel: ${image.colorModel.pixelSize}")
//        println("Transparency: $transparency")
//    } else {
//        println("The file $imageFile doesn't exist.")
//    }

//    // Finding the directory in the File with the biggest number of files in it.
//    val filesDirectory = File("C:\\Users\\Filip\\Downloads\\basedir\\basedir")
//    val listOfFiles = filesDirectory.walkTopDown().toList()
//    var filesInDir = 0
//    var biggestDir = ""
//
//    for (file in listOfFiles) {
//        if (file.isDirectory && file.name != "basedir") {
//            if ((file.listFiles()?.size ?: 0) > filesInDir) {
//                filesInDir = file.listFiles()?.size ?: 0
//                biggestDir = file.name
//            }
//        }
//    }
//
//    println("$biggestDir $filesInDir")

//}