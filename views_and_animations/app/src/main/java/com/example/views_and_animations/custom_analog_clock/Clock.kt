package com.example.views_and_animations.custom_analog_clock

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.views_and_animations.App
import com.example.views_and_animations.databinding.ClockCustomViewCroupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Clock @JvmOverloads constructor(
    context: Context = App.appContext,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = ClockCustomViewCroupBinding.inflate(LayoutInflater.from(context))

    init {
        //каждый поворот экрана объекты View и ViewGroup уничтожаются и создаются новые
        //так что init срабатывает каждый поворот
        addView(binding.root)
        //после того как мы надули на экране новую ViewGroup и соответственно новый ClockView,
        //надо чтобы наш ClockUseCase начал работать с новым объектом ClockView,
        //для этого в след. строке даем ему ссылку на binding.clockView
        useCase.updateClockView = binding.clockView::update

        useCase.setStartedState()// ClockView получает от ClockUseCase начальное состояние

        CoroutineScope(Dispatchers.Main).launch {
            useCase.stateClock.collect { clockIsOn ->
                binding.start.text = if (clockIsOn) "stop" else "start"
            }
        }

        binding.start.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (useCase.stateClock.value) useCase.stop()
                else useCase.start() }
        }

        binding.moreSpeed.setOnClickListener { useCase.accelerated(true) }

        binding.normSpeed.setOnClickListener { useCase.accelerated(false) }

        binding.reset.setOnClickListener { useCase.reset() }

        binding.plusHour.setOnClickListener { useCase.plusHour(1) }

        binding.minusHour.setOnClickListener { useCase.plusHour(-1) }

        Log.d("log", "init $this")
    }

    companion object {
        private val useCase = ClockUseCase(ClockView()::update)
    }
}