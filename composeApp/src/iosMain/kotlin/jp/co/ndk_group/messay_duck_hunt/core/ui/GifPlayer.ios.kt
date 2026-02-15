package jp.co.ndk_group.messay_duck_hunt.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFDataRef
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.valueForKey
import platform.ImageIO.CGImageSourceCopyPropertiesAtIndex
import platform.ImageIO.CGImageSourceCreateImageAtIndex
import platform.ImageIO.CGImageSourceCreateWithData
import platform.ImageIO.CGImageSourceGetCount
import platform.ImageIO.CGImageSourceRef
import platform.ImageIO.kCGImagePropertyGIFDelayTime
import platform.ImageIO.kCGImagePropertyGIFDictionary
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIImageOrientation
import platform.UIKit.UIImageRenderingMode
import platform.UIKit.UIViewContentMode
import platform.UIKit.UIImageView

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun GifPlayer(
    url: String,
    modifier: Modifier,
    contentScale: ContentScale ,
    contentDescription: String?
) {
    UIKitView(
        modifier = modifier,
        factory = {
            val imageView = UIImageView().apply {
                contentMode = when (contentScale) {
                    ContentScale.Fit -> UIViewContentMode.UIViewContentModeScaleAspectFit
                    ContentScale.Crop -> UIViewContentMode.UIViewContentModeScaleAspectFill
                    ContentScale.FillBounds -> UIViewContentMode.UIViewContentModeScaleToFill
                    else -> UIViewContentMode.UIViewContentModeScaleAspectFit
                }
                clipsToBounds = true
                backgroundColor = UIColor.clearColor
                opaque = false
            }

            val path = url.removePrefix("file://")
            val gifData = NSData.dataWithContentsOfFile(path) ?: return@UIKitView imageView

            val gifImage = UIImage.gifImageWithData(gifData)
            imageView.image = gifImage

            imageView
        },

        //WITHOUT THIS SET AS TRUE, gifs with Transparency will have white background!
        properties = UIKitInteropProperties(
            placedAsOverlay = true
        ),

    )
}

//Convert to UI Animated Image for ios
@OptIn(ExperimentalForeignApi::class)
fun UIImage.Companion.gifImageWithData(data: NSData): UIImage? {
    val dataRef = CFBridgingRetain(data) as? CFDataRef ?: return null
    val source = CGImageSourceCreateWithData(dataRef, null) ?: return null.also { CFBridgingRelease(dataRef) }

    try {
        val count = CGImageSourceGetCount(source).toInt()
        if (count == 0) return null

        val images = mutableListOf<UIImage>()
        val delays = mutableListOf<Double>()

        for (i in 0 until count) {
            val cgImage = CGImageSourceCreateImageAtIndex(source, i.toULong(), null)
                ?: continue

            val baseImage = UIImage.imageWithCGImage(
                cgImage,
                scale = 1.0,
                orientation = UIImageOrientation.UIImageOrientationUp
            )

            val frame = baseImage?.imageWithRenderingMode(
                UIImageRenderingMode.UIImageRenderingModeAlwaysOriginal
            )

            frame?.let {
                images.add(it)
                val delay = delayForImageAtIndex(i, source)
                delays.add(delay)
            }
        }

        if (images.isEmpty()) return null

        val duration = delays.sum()
        if (duration <= 0) return images.firstOrNull()

        val gcd = gcdForList(delays)
        val normalizedFrames = mutableListOf<UIImage>()

        images.forEachIndexed { index, frame ->
            val frameDuration = delays[index]
            val frameCount = if (gcd > 0) (frameDuration / gcd).toInt().coerceAtLeast(1) else 1
            repeat(frameCount) {
                normalizedFrames.add(frame)
            }
        }

        return animatedImageWithImages(
            images = normalizedFrames,
            duration = duration
        )
    } finally {
        CFBridgingRelease(dataRef)
    }
}
@OptIn(ExperimentalForeignApi::class)
private fun UIImage.Companion.delayForImageAtIndex(index: Int, source: CGImageSourceRef): Double {
    val cfProperties = CGImageSourceCopyPropertiesAtIndex(source, index.toULong(), null)
        ?: return 0.1

    val gifDict = (CFBridgingRelease(cfProperties) as? NSDictionary)
        ?.valueForKey((CFBridgingRelease(kCGImagePropertyGIFDictionary) as NSString).toString()) as? NSDictionary

    var delay = gifDict
        ?.valueForKey((CFBridgingRelease(kCGImagePropertyGIFDelayTime) as NSString).toString()) as? Double
        ?: 0.0

    if (delay < 0.02) delay = 0.1

    return delay
}

private fun UIImage.Companion.gcdForList(list: List<Double>): Double {
    if (list.isEmpty()) return 1.0
    if (list.size == 1) return list[0].coerceAtLeast(0.01)

    var gcd = list[0]
    list.drop(1).forEach { value ->
        gcd = gcdForPair(gcd, value)
    }
    return gcd.coerceAtLeast(0.01)
}

private fun UIImage.Companion.gcdForPair(a: Double, b: Double): Double {
    var x = a
    var y = b
    while (y > 0.0001) {
        val temp = y
        y = x % y
        x = temp
    }
    return x
}