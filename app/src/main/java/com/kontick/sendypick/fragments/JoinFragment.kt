package com.kontick.sendypick.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kontick.sendypick.R
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.databinding.FragmentJoinBinding
import com.kontick.sendypick.domain.models.joinCreateFragments.JoinFragmentViewModel
import com.kontick.sendypick.screens.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class JoinFragment : Fragment() {

    private lateinit var currentUser: User
    private lateinit var userViewModel: UserViewModel
    private lateinit var joinFragmentViewModel: JoinFragmentViewModel
    private var userRoomId = -1
    private var premiumStatus = 0
    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): JoinFragment {
            return JoinFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        val rootView: View = binding.root
        joinFragmentViewModel = ViewModelProvider(this).get(JoinFragmentViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userViewModel.readAllData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { usersRoom ->
            userRoomId = usersRoom[0].id
            premiumStatus = usersRoom[0].premiumStatus
            currentUser = User.Base(
                usersRoom[0].uid,
                usersRoom[0].name,
                usersRoom[0].phone,
                usersRoom[0].email,
                usersRoom[0].profilePhotoPath,
                usersRoom[0].roomCode
            )
        })

        binding.doneButton.setOnClickListener {
            val code = binding.codeEditText.text.toString().trim()
            if (code.isEmpty()) {
                binding.codeEditText.error = getString(R.string.write_the_code)
                binding.codeEditText.requestFocus()
                return@setOnClickListener
            }

            if (code.length < 20) {
                binding.codeEditText.error = getString(R.string.incorrect_code)
                binding.codeEditText.requestFocus()
                return@setOnClickListener
            }
            binding.doneButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                if(joinFragmentViewModel.isRoomExist(code)) {
                    joinFragmentViewModel.setUserRoomCode(currentUser.map(User.Mapper.UserId()), code)
                    joinFragmentViewModel.addCodeToUserRoomData(userViewModel, currentUser, userRoomId, code, premiumStatus)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, getString(R.string.welcome), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(context, MainActivity::class.java))
                    }
                } else
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), getString(R.string.there_is_no_such_code), Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.doneButton.visibility = View.VISIBLE
                    }
            }

        }

        binding.backImageView.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.join_create_frame, JoinOrCreateFragment.newInstance()).commit()
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}