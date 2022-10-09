package com.example.m16marsgram.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.m16marsgram.R
import com.example.m16marsgram.databinding.FragmentMainBinding
import com.example.m16marsgram.entity.RoverName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    val viewModel: MainViewModel by viewModels()
    private val adapter = RoverPhotoRecyclerAdapter { imgSrc -> onItemClicked(imgSrc) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = adapter

        binding.radio1.text = RoverName.SPIRIT.roverName.replaceFirstChar { it.uppercase() }
        binding.radio2.text = RoverName.OPPORTUNITY.roverName.replaceFirstChar { it.uppercase() }
        binding.radio3.text = RoverName.CURIOSITY.roverName.replaceFirstChar { it.uppercase() }
        binding.radio4.text = RoverName.PERSEVERANCE.roverName.replaceFirstChar { it.uppercase() }

        binding.numberPicker.isEnabled = false
        binding.numberPicker.maxValue = 500
        binding.numberPicker.minValue = 1
        binding.numberPicker.value = viewModel.currentSol

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkDialogMenuStatusAndUpdateView()

        binding.openCloseMenuText.setOnClickListener {
            viewModel.dialogMenuIsActive = !viewModel.dialogMenuIsActive
            checkDialogMenuStatusAndUpdateView()
        }

        binding.roverRadioGroup.setOnCheckedChangeListener { _, _ ->
            checkRover()?.let {
                if (checkRover() != viewModel.currentRover) {
                    viewModel.setRover(it)
                    viewModel.getUpdateSolAndCamParam()
                    loadRoverPhoto()
                }
            }
        }

        binding.numberPicker.setOnValueChangedListener { _, _, newVal ->
            viewModel.currentSol = newVal
            checkRover()?.let {
                viewModel.getUpdateSolAndCamParam()
                loadRoverPhoto()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.camerasToCurrentSolIsEnabled.collect {
                    binding.cameraSpinner.isEnabled = it
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.maxSolForMission.collect {
                    if (it != null) {
                        binding.numberPicker.isEnabled = true
                        binding.numberPicker.maxValue = it
                        binding.numberPicker.value = viewModel.currentSol
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.camerasToCurrentSol.collect {

                    val cameraSpinnerAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_item,
                        it
                    )
                    binding.cameraSpinner.adapter = cameraSpinnerAdapter

                        binding.cameraSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long,
                            ) {
                                viewModel.currentCamera = it[position]
                                checkRover()?.let {
                                    loadRoverPhoto()
                                    viewModel.getUpdateSolAndCamParam()
                                }

                            }
                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }
                    binding.cameraSpinner.setSelection(cameraSpinnerAdapter.getPosition(viewModel.currentCamera))
                }
            }
        }
    }

    private fun loadRoverPhoto() {
        viewModel.getPhotoFromRepo().onEach { pagingData ->
            adapter.submitData(pagingData)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun checkRover(): RoverName? {
        when (binding.roverRadioGroup.checkedRadioButtonId) {
            binding.radio1.id -> return RoverName.SPIRIT
            binding.radio2.id -> return RoverName.OPPORTUNITY
            binding.radio3.id -> return RoverName.CURIOSITY
            binding.radio4.id -> return RoverName.PERSEVERANCE
        }
        return null
    }

    private fun checkDialogMenuStatusAndUpdateView() {
        if (viewModel.dialogMenuIsActive) {
            binding.searchMenu.isVisible = true
            "Close menu".also { binding.openCloseMenuText.text = it }
        } else {
            binding.searchMenu.isVisible = false
            "Rover: ${viewModel.currentRover}  Sol: ${viewModel.currentSol}  Camera: ${viewModel.currentCamera}"
                .also { binding.openCloseMenuText.text = it }
        }
    }

    private fun onItemClicked(imgSrc: String) {
        parentFragmentManager.commit {
            replace(R.id.container, ImageFragment.newInstance(imgSrc))
            addToBackStack(ImageFragment::class.java.simpleName)
        }
    }
}