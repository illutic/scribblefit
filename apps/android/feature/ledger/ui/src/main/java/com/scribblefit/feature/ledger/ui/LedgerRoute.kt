package com.scribblefit.feature.ledger.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LedgerRoute() {
    val viewModel: LedgerViewModel = hiltViewModel()
    LedgerScreen(viewModel = viewModel)
}
