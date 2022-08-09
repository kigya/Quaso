package com.kigya.foundation.sideeffects.navigator.plugin

import com.kigya.foundation.sideeffects.SideEffectMediator
import com.kigya.foundation.sideeffects.navigator.Navigator
import com.kigya.foundation.views.BaseScreen

class NavigatorSideEffectMediator : SideEffectMediator<Navigator>(), Navigator {

    override fun launch(screen: BaseScreen) = target {
        it.launch(screen)
    }

    override fun goBack(result: Any?) = target {
        it.goBack(result)
    }

}