package kotNES.ui

import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.*
import java.awt.image.BufferedImage
import java.io.IOException

class CopyImageToClipboard : ClipboardOwner {
    override fun lostOwnership(clip: Clipboard, trans: Transferable) {
        println("Lost Clipboard Ownership")
    }

    fun copyToClipboard(image: BufferedImage) {
        val trans = TransferableImage(image)
        val c = Toolkit.getDefaultToolkit().systemClipboard
        c.setContents(trans, this)
    }

    private class TransferableImage(var image: Image) : Transferable {
        @Throws(UnsupportedFlavorException::class, IOException::class)
        override fun getTransferData(flavor: DataFlavor): Object {
            return if (flavor.equals(DataFlavor.imageFlavor) && image != null) image as Object
            else throw UnsupportedFlavorException(flavor)
        }

        override fun getTransferDataFlavors(): Array<DataFlavor?> {
            val flavors = arrayOfNulls<DataFlavor>(1)
            flavors[0] = DataFlavor.imageFlavor
            return flavors
        }

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
            val flavors = transferDataFlavors
            for (i in flavors.indices)
                if (flavor.equals(flavors[i]))
                    return true
            return false
        }
    }
}