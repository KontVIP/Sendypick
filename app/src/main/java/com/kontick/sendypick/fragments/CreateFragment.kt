package com.kontick.sendypick.fragments

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kontick.sendypick.R
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.databinding.FragmentCreateBinding
import com.kontick.sendypick.domain.models.joinCreateFragments.CreateFragmentViewModel
import com.kontick.sendypick.screens.MainActivity
import java.util.*


class CreateFragment : Fragment() {

    private lateinit var currentUser : User
    private lateinit var userViewModel: UserViewModel
    private lateinit var createFragmentViewModel: CreateFragmentViewModel
    private var userRoomId = -1
    private var premiumStatus = 0
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): CreateFragment {
            return CreateFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val rootView = binding.root
        val code = UUID.randomUUID().toString().subSequence(0, 20).toString()
        createFragmentViewModel = ViewModelProvider(this).get(CreateFragmentViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.codeTextView.text = code

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

        binding.codeTextView.setOnClickListener {
            createFragmentViewModel.copyToClipboard("code", context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.doneButton.setOnClickListener {
            createFragmentViewModel.setUserRoomCode(currentUser.map(User.Mapper.UserId()), code)
            createFragmentViewModel.addCodeToUserRoomData(userViewModel, currentUser, userRoomId, code, premiumStatus)
            Toast.makeText(context, getString(R.string.welcome), Toast.LENGTH_SHORT).show()
            startActivity(Intent(context, MainActivity::class.java))
        }

        rootView.findViewById<ImageView>(R.id.back_image_view).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.join_create_frame, JoinOrCreateFragment.newInstance()).commit()
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}