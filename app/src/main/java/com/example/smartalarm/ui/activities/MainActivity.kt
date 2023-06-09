package com.example.smartalarm.ui.activities

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smartalarm.R
import com.example.smartalarm.databinding.ActivityMainBinding
import com.example.smartalarm.ui.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private val notificationRequestCode = 100;
    private val vibrationRequestCode = 101;
    private val readExternalStorageRequestCode = 102;
    private val readMediaAudioRequestCode = 103;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission(
                notificationRequestCode,
                android.Manifest.permission.POST_NOTIFICATIONS,
                "показ уведомлений"
            )

            checkPermission(
                readMediaAudioRequestCode,
                android.Manifest.permission.READ_MEDIA_AUDIO,
                "чтение аудио-файлов для выбора мелодии"
            )
        }

        checkPermission(
            vibrationRequestCode,
            android.Manifest.permission.VIBRATE,
            "вызов вибрации"
        )

        checkPermission(
            readExternalStorageRequestCode,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            "чтение файлов для выбора мелодии"
        )

        val navController = supportFragmentManager
            .findFragmentById(R.id.fragment)
            ?.findNavController()

        binding.bottomNavigationView.setupWithNavController(navController!!)


        val sharedPreference = getSharedPreferences("holiday_is_complete", Context.MODE_PRIVATE)

        val holidayIsComplete = sharedPreference.getBoolean("is_complete", false)
        Log.i("grib", holidayIsComplete.toString())

        if (viewModel.holidayAlertNeed(holidayIsComplete)) {

            AlertDialog.Builder(binding.root.context)
                .setTitle("Скоро праздник!")
                .setMessage("${viewModel.getHolidayText()} праздник, не забудьте изменить будильники")
                .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                .setNegativeButton("Больше не показывать") { dialog, _ ->
                    sharedPreference.edit().apply {
                        putBoolean("is_complete", true)
                        apply()
                    }
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        if (viewModel.resetAlertNeed()) {
            sharedPreference.edit().apply {
                remove("is_complete")
                apply()
            }
        }

//        lifecycleScope.launch {
//            val rep = AlarmDbRepository(
//                AlarmsDB.getInstance(applicationContext)?.alarmsDao()!!
//            )
//            for (i in 0..200) {
//                rep.insertRecord(
//                    RecordsData(
//                        gameId = i % 3 + 1,
//                        gameName = "Тестовая игра 1",
//                        date = "01.02.$i",
//                        recordScore = i,
//                        recordTime = "05.20"
//                    )
//                )
//            }
//        }
    }

    private fun checkPermission(requestCode: Int, permission: String, name: String) {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
            }

            shouldShowRequestPermissionRationale(permission) -> {

                val builder = AlertDialog.Builder(this)
                builder.apply {
                    setMessage("Для корректной работы приложения необходимо разрешение на $name.")
                    setTitle("Необходимо разрешение")
                    setPositiveButton("Разрешить") { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(permission),
                            requestCode
                        )
                    }
                }

                val dialog = builder.create()
                dialog.show()
            }

            else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }
}
