/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.foundy.camstudy.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import io.foundy.auth.ui.LoginDestination
import io.foundy.auth.ui.loginGraph
import io.foundy.camstudy.ui.CamstudyTransitions
import io.foundy.home.ui.navigation.HomeDestination
import io.foundy.home.ui.navigation.homeGraph
import io.foundy.navigation.CamstudyDestination
import io.foundy.welcome.ui.WelcomeDestination
import io.foundy.welcome.ui.welcomeGraph

typealias OnPopUpAndNavigateCallBack = (
    destination: CamstudyDestination,
    popUpToDestination: CamstudyDestination,
    route: String
) -> Unit

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CamstudyNavHost(
    navController: NavHostController,
    navigate: (CamstudyDestination, String) -> Unit,
    popUpAndNavigate: OnPopUpAndNavigateCallBack,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = HomeDestination.route,
    enabledTransition: Boolean
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        exitTransition = {
            if (enabledTransition) {
                fadeOut(animationSpec = tween(CamstudyTransitions.DurationMilli))
            } else {
                ExitTransition.None
            }
        },
        enterTransition = {
            if (enabledTransition) {
                fadeIn(animationSpec = tween(CamstudyTransitions.DurationMilli))
            } else {
                EnterTransition.None
            }
        }
    ) {
        loginGraph(
            onReplaceToHome = {
                popUpAndNavigate(HomeDestination, LoginDestination, HomeDestination.route)
            },
            onReplaceToWelcome = {
                popUpAndNavigate(WelcomeDestination, LoginDestination, WelcomeDestination.route)
            }
        )
        welcomeGraph(
            onReplaceToHome = {
                popUpAndNavigate(HomeDestination, WelcomeDestination, HomeDestination.route)
            }
        )
        homeGraph()
    }
}
