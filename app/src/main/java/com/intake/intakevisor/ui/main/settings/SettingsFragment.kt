package com.intake.intakevisor.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.intake.intakevisor.LoginActivity
import com.intake.intakevisor.R
import com.intake.intakevisor.databinding.SettingsFragmentBinding
import com.intake.intakevisor.ui.main.MainActivity
import com.intake.intakevisor.ui.welcome.UserData

class SettingsFragment : Fragment() {

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var mainActivity: MainActivity

    var userData = UserData()
    private var lastLogoutClickTime: Long = 0 // Tracks the last logout click time

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity

        loadUserData()
        setupUI()
    }

    private fun setupUI() {
        setupDatePicker()

        binding.weightSeekBar.progress = userData.weight
        binding.weightTitle.text = getString(R.string.weightTitle, userData.weight)
        binding.heightSeekBar.progress = userData.height
        binding.heightTitle.text = getString(R.string.heightTitle, userData.height)
        binding.weightGoalSeekBar.progress = userData.goalWeight
        binding.weightGoalTitle.text = getString(R.string.settings_weight_goal_title, userData.goalWeight)
        hideActionButtons()

        binding.confirmSettingsBtn.setOnClickListener {
            userData.weight = binding.weightSeekBar.progress
            userData.height = binding.heightSeekBar.progress
            userData.goalWeight = binding.weightGoalSeekBar.progress
            userData.birthDate = binding.birthDatePicker.year.toString() + "-" +
                    (binding.birthDatePicker.month + 1).toString() + "-" +
                    binding.birthDatePicker.dayOfMonth.toString()

            saveUserData()
            hideActionButtons()
        }

        binding.cancelSettingsBtn.setOnClickListener {
            loadUserData()
            setupUI()
            hideActionButtons()
        }

        binding.weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum weight (e.g., can't be 0)
                val validProgress = if (progress < UserData.MIN_WEIGHT) UserData.MIN_WEIGHT else progress
                binding.weightSeekBar.progress = validProgress // reset progress if it's below the min
                binding.weightTitle.text = getString(R.string.weightTitle, validProgress)
                userData.weight = validProgress
                showActionButtons()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Update Height TextView
        binding.heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Enforce minimum height (e.g., can't be 0)
                val validProgress = if (progress < UserData.MIN_HEIGHT) UserData.MIN_HEIGHT else progress
                binding.heightSeekBar.progress = validProgress // reset progress if it's below the min
                binding.heightTitle.text = getString(R.string.heightTitle, validProgress)
                userData.height = validProgress
                showActionButtons()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.weightGoalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val validProgress = if (progress < UserData.MIN_WEIGHT) UserData.MIN_WEIGHT else progress
                binding.weightGoalSeekBar.progress = validProgress // reset progress if it's below the min
                binding.weightGoalTitle.text = getString(R.string.settings_weight_goal_title, validProgress)
                userData.goalWeight = validProgress
                showActionButtons()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.logoutButton.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            // Prevent spamming by checking if the last click was less than 2 seconds ago
            if (currentTime - lastLogoutClickTime < 2000) {
                return@setOnClickListener
            }

            lastLogoutClickTime = currentTime

            if (binding.confirmSettingsBtn.visibility == View.VISIBLE) {
                Toast.makeText(mainActivity, "Please, apply settings first.", Toast.LENGTH_SHORT).show() // Shorter duration
                return@setOnClickListener
            }

            // Start LoginActivity and pass a flag to indicate logout
            val intent = Intent(mainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("logout", true) // Add an extra flag to indicate logout
            startActivity(intent)

            // Finish the current activity to prevent going back to it
            mainActivity.finish()
        }

        loadAccountSection()
    }

    private fun loadUserData() {
        userData.weight = mainActivity.sharedPreferences.getInt("weight", userData.weight)
        userData.height = mainActivity.sharedPreferences.getInt("height", userData.height)
        userData.age = mainActivity.sharedPreferences.getInt("age", userData.age)
        userData.goalWeight = mainActivity.sharedPreferences.getInt("goalWeight", userData.goalWeight)
        userData.birthDate = mainActivity.sharedPreferences.getString("birthDate", userData.birthDate)!!
    }

    private fun setupDatePicker() {
        val birthDate = mainActivity.sharedPreferences.getString("birthDate", "2000-01-01")
        val (year, month, day) = birthDate!!.split("-").map { it.toInt() }

        binding.birthDatePicker.updateDate(year, month - 1, day) // Month is 0-indexed
        binding.birthDatePicker.setOnDateChangedListener { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            userData.birthDate = selectedDate
            showActionButtons()
        }
    }

    private fun showActionButtons() {
        binding.confirmSettingsBtn.visibility = View.VISIBLE
        binding.cancelSettingsBtn.visibility = View.VISIBLE
    }

    private fun hideActionButtons() {
        binding.confirmSettingsBtn.visibility = View.GONE
        binding.cancelSettingsBtn.visibility = View.GONE
    }

    private fun saveUserData() {
        with(mainActivity.sharedPreferences.edit()) {
            putInt("weight", userData.weight)
            putInt("height", userData.height)
            putInt("age", userData.age)
            putString("birthDate", userData.birthDate)
            putInt("goalWeight", userData.goalWeight)
            apply() // Apply changes asynchronously
        }
    }

    private fun loadAccountSection() {
        val account = FirebaseAuth.getInstance().currentUser
        if (account != null) {
            binding.googleAccountEmail.text = account.email
            getAndLoadUserIcon()
            binding.googleAccountSection.visibility = View.VISIBLE
        } else {
            binding.googleAccountSection.visibility = View.GONE
        }
    }

    private fun getAndLoadUserIcon() {
        val account = FirebaseAuth.getInstance().currentUser
        if (account != null && account.photoUrl != null) {
            Glide.with(this)
                .load(account.photoUrl)
                .into(binding.googleIcon)
        }
    }
}