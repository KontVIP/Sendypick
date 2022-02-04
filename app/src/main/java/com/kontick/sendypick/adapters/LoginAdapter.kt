package com.kontick.sendypick.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.kontick.sendypick.R
import com.kontick.sendypick.fragments.LoginTabFragment
import com.kontick.sendypick.fragments.SignupTabFragment

class LoginAdapter(fm: FragmentManager?, private val context: Context, var totalTabs: Int) :
    FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                LoginTabFragment()
            }
            else -> {
                SignupTabFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            context.getString(R.string.sign_in)
        } else {
            context.getString(R.string.sign_up)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
