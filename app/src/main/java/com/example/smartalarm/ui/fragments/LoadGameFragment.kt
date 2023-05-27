package com.example.smartalarm.ui.fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.Navigation
import com.example.smartalarm.R
import com.example.smartalarm.databinding.FragmentLoadGameBinding
import com.example.smartalarm.services.AlarmVibrator
import com.example.smartalarm.ui.viewmodels.LoadGameViewModel
import java.time.ZoneId

class LoadGameFragment : Fragment() {

    private lateinit var binding: FragmentLoadGameBinding
    private lateinit var viewModel: LoadGameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadGameBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoadGameViewModel::class.java]

//        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        AlarmVibrator.stop()

        val bundle = Bundle()

        viewModel.currentGame.observe(viewLifecycleOwner) {
            val navController = Navigation.findNavController(binding.root)

            bundle.putLong("alarm id", requireActivity().intent.getLongExtra("alarm id", -1))
            bundle.putBoolean("test", false)

            if (it.isEmpty()) {
                navController.navigate(R.id.action_loadGameFragment_to_gameResultFragment, bundle)
            } else {
                bundle.putInt("difficulty", it[1])
                when (it[0]) {
                    1 -> navController.navigate(
                        R.id.action_loadGameFragment_to_calcGameFragment,
                        bundle
                    )

                    2 -> navController.navigate(
                        R.id.action_loadGameFragment_to_taskGameFragment,
                        bundle
                    )
                }
            }
        }

        bundle.putLong("start time", requireActivity().intent.getLongExtra("start time", 0L))

        viewModel.getAlarm(requireActivity().intent.getLongExtra("alarm id", -1))

        return binding.root
    }

}