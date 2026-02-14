package jp.co.ndk_group.messay_duck_hunt.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFDataRef
import platform.CoreGraphics.CGImageRef
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.valueForKey
import platform.ImageIO.CGImageSourceCopyPropertiesAtIndex
import platform.ImageIO.CGImageSourceCreateImageAtIndex
import platform.ImageIO.CGImageSourceCreateWithData
import platform.ImageIO.CGImageSourceGetCount
import platform.ImageIO.CGImageSourceRef
import platform.ImageIO.kCGImagePropertyGIFDelayTime
import platform.ImageIO.kCGImagePropertyGIFDictionary
import platform.UIKit.UIImage
import platform.UIKit.UIViewContentMode
import platform.UIKit.UIImageView

/// Created by Aabhash Shakya on 14/02/2026

@Composable
actual fun GifPlayer(
    url: String,
    modifier: Modifier,
    contentScale: ContentScale,
    contentDescription: String?
) {
    UIKitView(
        modifier = modifier,
        factory = {
            val imageView = UIImageView()
            imageView.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
            imageView.clipsToBounds = true

            val path = url.removePrefix("file://")
            val gifData = NSData.create(contentsOfFile = path)
            val gifImage = gifData?.let { UIImage.gifImageWithData(it) }

            imageView.image = gifImage
            imageView
        }
    )
}


//additional setup to make sure GIF plays smoothly
@OptIn(ExperimentalForeignApi::class)
fun UIImage.Companion.gifImageWithData(data: NSData?): UIImage? {
    return runCatching {
        val dataRef = CFBridgingRetain(data) as? CFDataRef
        val source = CGImageSourceCreateWithData(dataRef, null) ?: return null
        val count = CGImageSourceGetCount(source).toInt()
        val images = mutableListOf<CGImageRef>()
        val delays = mutableListOf<Double>()

        for (i in 0 until count) {
            val image = CGImageSourceCreateImageAtIndex(source, i.toULong(), null)
            if (image != null) {
                images.add(image)
            }

            val delaySeconds = delayForImageAtIndex(i, source)
            delays.add(delaySeconds * 400.0) // s to ms
        }

        println("images=${images.count()}")

        val duration = delays.sum()
        println("duration=$duration")

        val gcd = gcdForList(delays)
        println("gcd=$gcd")
        val frames = mutableListOf<UIImage>()

        for (i in 0 until count) {
            val frame = UIImage.imageWithCGImage(images[i])
            val frameCount = (delays[i] / gcd).toInt()
            for (f in 0 until frameCount) {
                frames.add(frame)
            }
        }
        println("frames=${frames.count()}")

        val animation = UIImage.animatedImageWithImages(frames, duration / 1000.0) ?: return null
        animation
    }.onFailure { it.printStackTrace() }.getOrNull()
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.Companion.delayForImageAtIndex(index: Int, source: CGImageSourceRef): Double {
    var delay: Double

    val cfProperties = CGImageSourceCopyPropertiesAtIndex(source, index.toULong(), null)
    val gifKey = (CFBridgingRelease(kCGImagePropertyGIFDictionary) as NSString).toString()
    val gifInfo =
        (CFBridgingRelease(cfProperties) as? NSDictionary)?.valueForKey(gifKey) as? NSDictionary

    delay =
        gifInfo?.valueForKey((CFBridgingRelease(kCGImagePropertyGIFDelayTime) as NSString).toString()) as? Double
            ?: 0.0

    if (delay < 0.1) {
        delay = 0.1
    }

    return delay
}

private fun UIImage.Companion.gcdForPair(_a: Int?, _b: Int?): Int {
    var a = _a
    var b = _b
    if (b == null || a == null) {
        return b ?: (a ?: 0)
    }

    if (a < b) {
        val c = a
        a = b
        b = c
    }

    var rest: Int
    while (true) {
        rest = a!! % b!!
        if (rest == 0) {
            return b
        } else {
            a = b
            b = rest
        }
    }
}

private fun UIImage.Companion.gcdForList(list: List<Double>): Double {
    if (list.isEmpty()) return 1.0
    var gcd = list[0]
    list.onEach {
        gcd = UIImage.gcdForPair(it.toInt(), gcd.toInt()).toDouble()
    }
    return gcd
}