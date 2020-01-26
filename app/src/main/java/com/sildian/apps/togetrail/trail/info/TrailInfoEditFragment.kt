package com.sildian.apps.togetrail.trail.info


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sildian.apps.togetrail.R

/**
 * A simple [Fragment] subclass.
 */
class TrailInfoEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trail_info_edit, container, false)
    }


}
