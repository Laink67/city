package ru.laink.city.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.laink.city.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.map_image)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_home_dest_to_map, null)
        )

        view.findViewById<FloatingActionButton>(R.id.add_fab)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_home_dest_to_add_message, null)
        )

    }
}