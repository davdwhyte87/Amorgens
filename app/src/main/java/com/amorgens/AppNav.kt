package com.amorgens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amorgens.auth.data.AuthViewModel
import com.amorgens.auth.presentation.loginScreen
import com.amorgens.auth.presentation.logoutScreen
import com.amorgens.auth.presentation.preLoginScreen
import com.amorgens.auth.presentation.signUpScreen
import com.amorgens.feed.data.FeedViewModel
import com.amorgens.feed.presentation.createPostScreen
import com.amorgens.home.presentation.HomeScreen
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.presentation.ShopCoinsScreen
import com.amorgens.trade.presentation.addPaymentMethodScreen
import com.amorgens.trade.presentation.createSellOrderScreen
import com.amorgens.trade.presentation.myOrdersScreen
import com.amorgens.trade.presentation.paymentOptionScreen
import com.amorgens.trade.presentation.singleOrderScreen
import com.amorgens.trade.presentation.singleSellOrderScreen
import com.amorgens.wallet.data.WalletViewModel
import com.amorgens.wallet.presentation.AddWalletScreen
import com.amorgens.wallet.presentation.NewWalletScreen
import com.amorgens.wallet.presentation.SingleWalletScreen
import com.amorgens.wallet.presentation.TransferScreen
import com.amorgens.wallet.presentation.WalletScreen

@Composable
fun AppNav(
    navController:NavHostController,
    walletViewModel: WalletViewModel,
    orderViewModel: OrderViewModel,
    authViewModel: AuthViewModel,
    feedViewModel: FeedViewModel
){

    NavHost(navController = navController, startDestination = NavScreen.HomeScreen.route) {
//        composable(route=NavScreen.HomeScreen.route){
//            HomeScreen(navController)
//        }
        composable(route=NavScreen.WalletScreen.route){
            WalletScreen(navController, walletViewModel)
        }
        composable(route=NavScreen.SingleWalletScreen.route+"/{address}"){navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address != null){
                SingleWalletScreen(address, navController, walletViewModel)
            }
        }
        composable(route=NavScreen.TransferScreen.route+"/{address}"){ navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address != null){
                TransferScreen( navController, address, walletViewModel)
            }
        }

        composable(route=NavScreen.CreatePostScreen.route){
            createPostScreen(navController, feedViewModel)
        }

        composable(route=NavScreen.SingleOrderScreen.route+"/{id}"){navBackStack->
            val id = navBackStack.arguments?.getString("id")
            if(id!=null){
                singleOrderScreen(navController, orderViewModel, id)
            }
        }
        
        composable(route=NavScreen.NewWalletScreen.route){
            NewWalletScreen(navController = navController, walletViewModel)
        }

//        composable(route=NavScreen.CreateSellOrderScreen.route){
//            createSellOrderScreen(navController, orderViewModel)
//        }


        composable(route=NavScreen.AddWalletScreen.route){
            AddWalletScreen(navController = navController, walletViewModel)
        }

        // jut him kin mute turn the
        composable(route=NavScreen.ShopCoinsScreen.route+"/{address}"){navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address !=null){
                ShopCoinsScreen(navController,orderViewModel, address )
            }
        }

        composable(route=NavScreen.SingleSellOrderScreen.route+ "/{id}"){navBackStack->
            val id = navBackStack.arguments?.getString("id")
            if(id!=null) {
                singleSellOrderScreen(navController,orderViewModel, id)
            }
        }
        composable(route=NavScreen.MyOrdersScreen.route){
            myOrdersScreen(navController,orderViewModel )
        }

        composable(route=NavScreen.PreLoginScreen.route){
            preLoginScreen(navController)
        }

        composable(route=NavScreen.LoginScreen.route){
            loginScreen(navController, authViewModel)
        }
        composable(route=NavScreen.SignupScreen.route){
            signUpScreen(authViewModel,navController)
        }
        
        composable(NavScreen.LogoutScreen.route){
            logoutScreen(navHostController = navController, authViewModel)
        }

        composable(NavScreen.MyPaymentMethodsScreen.route){
            paymentOptionScreen(orderViewModel, navController)
        }

        composable(NavScreen.CreatePaymentMethodScreen.route){
            addPaymentMethodScreen(navController, orderViewModel)
        }

        protectedComposable(
            navController,
            false,
            NavScreen.CreateSellOrderScreen.route+"/{address}",
            authViewModel
        ) {
            val address = it.arguments?.getString("address")
            if(address!=null) {
                createSellOrderScreen(navController, orderViewModel, address)
            }
        }

        protectedComposable(
            navController,
            false,
            NavScreen.HomeScreen.route,
            authViewModel
        ) {
            HomeScreen(navController, feedViewModel)
        }

    }
}
fun NavGraphBuilder.protectedComposable(
    navController: NavController,
    isLoggedIn: Boolean,
    route: String,
    authViewModel: AuthViewModel,
    content: @Composable (androidx.navigation.NavBackStackEntry) -> Unit
){
    composable(route) {backStackEntry->

//        if (!authViewModel.isLoggedIn()){
//            navController.navigate("pre_login") {
//                popUpTo(NavScreen.HomeScreen.route) { inclusive = false }
//            }
//        }
        if (authViewModel.isLoggedIn()) {
            content(backStackEntry)
        } else {
            navController.navigate("pre_login") {
                popUpTo(NavScreen.HomeScreen.route) { inclusive = false }
            }
        }
    }
}