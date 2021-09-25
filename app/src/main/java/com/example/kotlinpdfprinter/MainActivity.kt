package com.example.kotlinpdfprinter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.itextpdf.text.pdf.interfaces.PdfDocumentActions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.jvm.Throws

class MainActivity : AppCompatActivity() {
    val file_name = "test_pdf.pdf"

    var PERMISSION_ALL = 1
    var PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        if (!hasPermissions(this@MainActivity, * PERMISSIONS)) {
            ActivityCompat.requestPermissions(this@MainActivity, PERMISSIONS, PERMISSION_ALL)
        }

        val file = File(getExternalFilesDir(null)!!.absolutePath, "pdfsdcard_location")
        if (!file.exists()) {
            file.mkdir()
        }

/*        Dexter.withActivity(this@MainActivity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    btn_pdf.setOnClickListener {
                        createPDFFile(Common.getAppPath(this@MainActivity) + file_name)
                        Toast.makeText(this@MainActivity, "abc", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {

                }
            })*/
        btn_pdf.setOnClickListener {
            createPDFFile(Common.getAppPath(this@MainActivity) + file_name)
            Toast.makeText(this@MainActivity, "abc", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createPDFFile(path: String) {
        if (File(path).exists())
            File(path).delete()
        try {
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(path))
            document.open()
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("AAM POWER")
            document.addCreator("By")

            val colorAccent = BaseColor(0, 153, 203, 255)
            val headingFontSize = 20.0f
            val valueFontSize = 26.0f
            val fontName = BaseFont.createFont(
                "assets/fonts/StickNoBills-Regular.ttf",
                "UTF-8",
                BaseFont.EMBEDDED
            )

            val titleStyle = Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "Detail", Element.ALIGN_CENTER, titleStyle)
            val headingStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            addNewItem(document, "Order NO", Element.ALIGN_LEFT, headingStyle)
            val valueStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            addNewItem(document, "#122132", Element.ALIGN_LEFT, valueStyle)
            addLineSeperator(document)
            addNewItem(document, "Date", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "03/06/2021", Element.ALIGN_LEFT, valueStyle)
            addLineSeperator(document)
            addNewItem(document, "AccountName", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "Dealer", Element.ALIGN_LEFT, valueStyle)
            addLineSeperator(document)
            addNewItem(document, "Product Detail", Element.ALIGN_CENTER, titleStyle)
            addLineSeperator(document)
            addNewItemWithLeftAndRight(document, "Pizza", "(0.0%)", titleStyle, valueStyle)
            addNewItemWithLeftAndRight(document, "12.0*1000", "12000.0", titleStyle, valueStyle)
            addLineSeperator(document)
            addNewItemWithLeftAndRight(document, "Pizza", "(0.0%)", titleStyle, valueStyle)
            addNewItemWithLeftAndRight(document, "12.0*1000", "12000.0", titleStyle, valueStyle)
            addLineSeperator(document)
            addLinSpace(document)
            addLinSpace(document)
            addNewItemWithLeftAndRight(document, "Total", "240000.0)", titleStyle, valueStyle)
            document.close()
            Toast.makeText(this@MainActivity, "Success ", Toast.LENGTH_SHORT).show()
            printPDF()
        } catch (e: Exception) {
            Log.e("HMH", "createPDFFile: ${e.message}")
        }
    }

    private fun printPDF() {
        val printManger = getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printAdapter = PdfDocumentAdapter(this@MainActivity, Common.getAppPath(this@MainActivity) + file_name)
            printManger.print("Document", printAdapter, PrintAttributes.Builder().build())
        }catch (e:Exception){
            Log.e("HMH", "createPDFFile: ${e.message}")
        }
    }

    @Throws(DocumentException::class)
    private fun addNewItemWithLeftAndRight(
        document: Document,
        textLeft: String,
        textRight: String,
        leftStyle: Font,
        rightStyle: Font
    ) {
        val chunkTextLeft = Chunk(textLeft, leftStyle)
        val chunkTextRight = Chunk(textRight, rightStyle)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)
    }

    @Throws(DocumentException::class)
    private fun addLineSeperator(document: Document) {
        val lineSeparator = LineSeparator()
        lineSeparator.lineColor = BaseColor(0, 0, 0, 68)
        addLinSpace(document)
        document.add(Chunk(lineSeparator))
        addLinSpace(document)
    }

    private fun addLinSpace(document: Document) {
        document.add(Paragraph(""))
    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, align: Int, style: Font) {
        val chunk = Chunk(text, style)
        val p = Paragraph(chunk)
        p.alignment = align
        document.add(p)
    }


    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (!(Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context == null || permissions == null)) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }
}


