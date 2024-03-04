package com.example.views_and_animations

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.views_and_animations.databinding.ActivityClockBinding

class ClockActivity: AppCompatActivity() {
    private lateinit var binding: ActivityClockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClockBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}