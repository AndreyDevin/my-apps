package com.example.views_and_animations.custom_analog_clock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.example.views_and_animations.App
import kotlin.math.cos
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context = App.appContext,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
): View(context, attrs, defStyleAttr) {

    private var isInit = false // станет true, когда часы будут инициализированы в onDraw

    private val clockHours = listOf(1,2,3,4,5,6,7,8,9,10,11,12)
    private var angle = 0.0 // будем перезаписывать в циклах, чтобы не создавать кучу объектов
    private val paint = Paint()
    private var rect = Rect() // Rect - прямоугольник

    private var viewHeight = 0F
    private var viewWidth = 0F
    private val paddingFromCircle = 50F // расстояние от границы круга
    private var hourHandTruncation = 0F // усечение длины часовой стрелки
    private var handTruncation = 0F // усечение минутной и секундной стрелок
    private var circleRadius = 0F // задаёт радиус нарисованного круга

    private var sec = 0
    private var min = 0
    private var hour = 0
    private var timeAsString = "00 : 00 : 00" // для поля, где выводим время в цифровом формате

    private var clockIsAccelerated = false // true если время ускорено
    private var clockIsOn = false

    fun update(s: Int, m: Int, h: Int, clockState: Boolean, acceleratedState: Boolean) {
        sec = s
        min = m
        hour = h % 12
        timeAsString = "${timeToString(h)} : ${timeToString(m)} : ${timeToString(s)}"
        clockIsOn = clockState
        clockIsAccelerated = acceleratedState
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //onDraw будет вызываться каждый раз, когда в сеттере var sec срабатывает invalidate()
        //для улучшения производительности в блоке if(!isInit) один раз выполним код и зададим
        //значения, которые не меняются
        if (!isInit) {
            viewHeight = height.toFloat()
            viewWidth = width.toFloat()
            val minAttr = viewHeight.coerceAtMost(viewWidth)
            circleRadius = minAttr / 2 - paddingFromCircle

            val fontSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 21f, resources.displayMetrics)
            paint.textSize = fontSize

            paint.strokeWidth = 4F // ширина обводки
            paint.isAntiAlias = true // сглаживание краёв рисуемого объекта

            handTruncation = minAttr / 20
            hourHandTruncation = minAttr / 17

            isInit = true
        }

        //рисуем окружность серого цвета (фон часов)
        paint.color = Color.DKGRAY
        paint.style = Paint.Style.FILL //стиль окрашивания - "заполнить" (залить цветом)
        canvas.drawCircle(
            viewWidth / 2F,
            viewHeight / 2F,
            circleRadius + paddingFromCircle,
            paint
        )

        //рисуем точку в центре (ось стрелок)
        paint.color = Color.WHITE
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, 12F, paint)

        //рисуем циферблат
        for (hour in clockHours) {
            //строка ниже скорее всего выранивает цифры на циферблате с учетом, что есть двузначные
            paint.getTextBounds(hour.toString(), 0, hour.toString().length, rect)

            angle = Math.PI / 6 * (hour - 3)
            canvas.drawText(
                hour.toString(),
                (viewWidth / 2 + cos(angle) * circleRadius - rect.width() / 2).toFloat(),
                (viewHeight / 2 + sin(angle) * circleRadius + rect.height() / 2).toFloat(),
                paint
            )
        }

        //рисуем минутные точки
        for (i in 1..60) {
            angle = Math.PI * i / 30 - Math.PI / 2
            canvas.drawPoint(
                (viewWidth / 2 + cos(angle) * (circleRadius - handTruncation)).toFloat(),
                (viewWidth / 2 + sin(angle) * (circleRadius - handTruncation)).toFloat(),
                paint
            )
        }

        //рисуем стрелки часов
        drawHandLine(canvas,hour * 5 + min/12F, isHour = true, isSecond = false) //draw hours
        drawHandLine(canvas, min + sec/60F, isHour = false, isSecond = false) //draw minutes
        drawHandLine(canvas, sec.toFloat(), isHour = false, isSecond = true) //draw seconds

        //рисуем поле для времени в цифровом формате
        paint.color = if (clockIsOn || sec != 0 || hour != 0) Color.GREEN else Color.GRAY
        canvas.drawText(
            timeAsString,
            viewWidth * 0.332F,
            viewHeight * 0.75F,
            paint
        )

        //если время ускорено, поле "x100 красного цвета, иначе серого
        paint.color = if (clockIsAccelerated) Color.RED else Color.GRAY
        canvas.drawText(
            "x100",
            viewWidth / 4,
            viewHeight / 2 + paint.textSize / 3,
            paint
        )
    }

    private fun drawHandLine(canvas: Canvas, moment: Float, isHour: Boolean, isSecond: Boolean) {
        angle = Math.PI * moment / 30 - Math.PI / 2
        val handRadius =
            if (isHour) circleRadius - handTruncation - hourHandTruncation
            else circleRadius - handTruncation
        if (isSecond) paint.color = Color.YELLOW
        canvas.drawLine(
            viewWidth / 2,
            viewHeight / 2,
            (viewWidth / 2 + cos(angle) * handRadius).toFloat(),
            (viewHeight / 2 + sin(angle) * handRadius).toFloat(),
            paint
        )
    }

    private fun timeToString(t: Int): String {
        return if (t in 0..9) "0$t" else t.toString()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.d("log", "onFinishInflate() id: ${this.id} obj: $this")
    }
}