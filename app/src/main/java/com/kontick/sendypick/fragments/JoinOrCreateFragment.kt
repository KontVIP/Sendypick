package com.kontick.sendypick.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kontick.sendypick.R

class JoinOrCreateFragment() : Fragment() {

    companion object {
        fun newInstance(): JoinOrCreateFragment {
            return JoinOrCreateFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView : View = inflater.inflate(R.layout.fragment_join_or_create, container, false)

        val fragmentManager = parentFragmentManager
        rootView.findViewById<Button>(R.id.create_button).setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.join_create_frame, CreateFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        rootView.findViewById<Button>(R.id.join_button).setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.join_create_frame, JoinFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }


        return rootView
    }

}