package com.example.at.views.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.at.R
import com.example.at.databinding.FragmentTrainingSettingsBinding
import android.graphics.drawable.BitmapDrawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint


import android.widget.TextView

import android.graphics.drawable.Drawable
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.at.models.Store
import com.example.at.models.TrainingSettingsModel


class TrainingSettingsFragment : Fragment() {
    private var _binding: FragmentTrainingSettingsBinding? = null
    private val binding get() = _binding!!

    var thumbView: View? = null

    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    private val maxNoise: Int = 1000

    private val linesX: Int = 16
    private val linesY: Int = 15
    private val lineWidth: Float = 10.toFloat()
    private val linePadding: Float = 40.toFloat()
    private val startOffset: Float = 30.toFloat()
    private val endOffset: Float = 30.toFloat()
    private val width: Int = (startOffset + endOffset + linePadding * linesX - 1).toInt()
    private val height: Int = (startOffset + endOffset + linePadding * linesY - 1).toInt()
    private var currentX = 1
    private var currentY = 2
    private val activeColor = Color.RED
    private val passiveColor = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thumbView = LayoutInflater.from(activity)
            .inflate(R.layout.layout_seekbar_thumb, null, false)

    }

    override fun onStart() {
        super.onStart()
        setUpBindings()
        binding.seekbarErrors.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                // You can have your own calculation for progress
                seekBar.thumb = getThumb(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        drawBase()

        drawLine(currentX, false, activeColor)
        drawLine(currentY, true, activeColor)

    }

    private fun drawBase() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = lineWidth
        val textPaint = Paint()
        textPaint.textSize = 30F
        var x1 = startOffset
        var y1 = startOffset
        var x2 = startOffset
        var y2 = height.toFloat() - endOffset

        for (i in 1..linesX) {
            canvas!!.drawLine(x1, y1, x2, y2, paint)
            canvas!!.drawText(i.toString(), x1 - 10, y1 - 10, textPaint)
            x1 += linePadding
            x2 += linePadding
        }

        x1 = startOffset
        y1 = startOffset
        x2 = width.toFloat() - endOffset
        y2 = startOffset

        for (i in 1..linesY) {
            canvas!!.drawLine(x1, y1, x2, y2, paint)
            canvas!!.drawText(i.toString(), x1 - 35, y1, textPaint)
            y1 += linePadding
            y2 += linePadding
        }

        binding.imageView.setImageBitmap(bitmap)
    }

    private fun drawLine(num: Int, isVertical: Boolean, color: Int) {
        // true - vertical
        // false - horizontal

        if (num >= 0){
            val paint = Paint()
            paint.color = color
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = lineWidth

            var offset = currentX
            if (currentX < 0) {
                offset = 0
            }


            var x1 = startOffset + linePadding * num
            var y1 = startOffset + offset * linePadding
            var x2 = startOffset + linePadding * num
            var y2 = height.toFloat() - endOffset

            if (!isVertical) {
                x1 = startOffset
                y1 = startOffset + linePadding * num
                var offset = currentY
                if (currentY < 0) {
                    offset = 0
                }
                x2 = width.toFloat() - endOffset - (linesY - offset+1) * linePadding
                y2 = startOffset + linePadding * num
            }

            canvas!!.drawLine(x1, y1, x2, y2, paint)

            binding.imageView.setImageBitmap(bitmap)
        }


    }

    private fun drawLineFull(num: Int, isVertical: Boolean, color: Int) {
        // true - vertical
        // false - horizontal

        if (num >= 0){
            val paint = Paint()
            paint.color = color
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = lineWidth

            var x1 = startOffset + linePadding * num
            var y1 = startOffset
            var x2 = startOffset + linePadding * num
            var y2 = height.toFloat() - endOffset

            if (!isVertical) {
                x1 = startOffset
                y1 = startOffset + linePadding * num
                x2 = width.toFloat() - endOffset
                y2 = startOffset + linePadding * num
            }

            canvas!!.drawLine(x1, y1, x2, y2, paint)

            binding.imageView.setImageBitmap(bitmap)
        }
    }

    fun getThumb(progress: Int): Drawable? {
        (thumbView!!.findViewById(R.id.tvProgress) as TextView).text = progress.toString() + ""
        thumbView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            thumbView!!.getMeasuredWidth(),
            thumbView!!.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        thumbView!!.layout(0, 0, thumbView!!.getMeasuredWidth(), thumbView!!.getMeasuredHeight())
        thumbView!!.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

    private fun setUpBindings() {
        binding.row.addTextChangedListener() {
            drawLineFull(currentX, false, passiveColor)
            if (binding.row.text.toString() != "") {
                if ((binding.row.text.toString()
                        .toInt() < linesX) and (binding.row.text.toString()
                        .toInt() > 0)
                ) {
                    binding.errorText.visibility = View.INVISIBLE
                    currentX = binding.row.text.toString().toInt() - 1
                    drawLine(currentX, false, activeColor)
                    drawLineFull(currentY, true, passiveColor)
                    drawLine(currentY, true, activeColor)
                } else if (binding.row.text.toString()
                        .toInt() != 0){
                    binding.errorText.visibility = View.VISIBLE
                } else {
                    currentX = -1
                }
            }
        }

        binding.col.addTextChangedListener {
            drawLineFull(currentY, true, passiveColor)
            if (binding.col.text.toString() != "") {
                if ((binding.col.text.toString()
                        .toInt() < linesY) and (binding.col.text.toString()
                        .toInt() > 0)
                ) {
                    binding.errorText.visibility = View.INVISIBLE

                    currentY = binding.col.text.toString().toInt() - 1
                    drawLine(currentY, true, activeColor)
                    drawLineFull(currentX, false, passiveColor)
                    drawLine(currentX, false, activeColor)
                } else if (binding.col.text.toString()
                        .toInt() != 0){
                    binding.errorText.visibility = View.VISIBLE
                } else {
                    currentY = -1
                }
            }
        }
        binding.buttonNext.setOnClickListener {
            if ((binding.row.text.toString() != "") and (binding.errorText.visibility == View.INVISIBLE)
                and (binding.col.text.toString() != "")
            ) {
                val model = TrainingSettingsModel(
                    currentX, currentY,
                    binding.seekbarErrors.progress
                )
                Store.currentSettings = model
                Toast.makeText(activity,"called 2", Toast.LENGTH_SHORT).show()
                val msg: String = model.convertToString() + '\n'
                (activity as MainActivity?)!!.connectedThread!!.write(msg)

                /*
                while (Store.ArduinoMsg == null) {

                }

                // should receive message like "0 noise",
                // where 0 is state type
                // if noise is a big value, should repeat initialization
                val zeroMsg = Store.parseZeroMsg(Store.ArduinoMsg!!)
                Store.ArduinoMsg = null
                if (zeroMsg.noise > maxNoise) {
                    Toast.makeText(
                        (activity as MainActivity),
                        "noise value is ${zeroMsg.noise}, please, correct the mat " +
                                "and try again", Toast.LENGTH_LONG
                    ).show()
                } else { */

                    val f = TrainingFragment()
                    (activity as MainActivity).replaceFragment(f)
                //}
            }
        }

    }

}
