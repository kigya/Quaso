package com.kigya.quaso

import android.os.Bundle
import com.kigya.foundation.sideeffects.SideEffectPluginsManager
import com.kigya.foundation.sideeffects.dialogs.plugin.DialogsPlugin
import com.kigya.foundation.sideeffects.intents.plugin.IntentsPlugin
import com.kigya.foundation.sideeffects.navigator.plugin.NavigatorPlugin
import com.kigya.foundation.sideeffects.navigator.plugin.StackFragmentNavigator
import com.kigya.foundation.sideeffects.resources.plugin.ResourcesPlugin
import com.kigya.foundation.sideeffects.toasts.plugin.ToastsPlugin
import com.kigya.foundation.views.activity.BaseActivity
import com.kigya.quaso.views.home.HomeFragment

/**
 * Container for all screens
 */
class MainActivity : BaseActivity() {

    override fun registerPlugins(manager: SideEffectPluginsManager) =
        with(receiver = manager) {
            val navigator = createNavigator()
            register(ToastsPlugin())
            register(ResourcesPlugin())
            register(NavigatorPlugin(navigator))
            register(DialogsPlugin())
            register(IntentsPlugin())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    private fun createNavigator() = StackFragmentNavigator(
        containerId = R.id.fragmentContainer,
        animations = StackFragmentNavigator.Animations(
            enterAnim = R.anim.enter,
            exitAnim = R.anim.exit,
            popEnterAnim = R.anim.pop_enter,
            popExitAnim = R.anim.pop_exit
        ),
        initialScreenCreator = { HomeFragment.Screen() }
    )

}