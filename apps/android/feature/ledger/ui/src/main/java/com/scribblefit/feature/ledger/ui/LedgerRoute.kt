package com.scribblefit.feature.ledger.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LedgerRoute() {
    val viewModel: LedgerViewModel = viewModel()
    LedgerScreen(viewModel = viewModel)
}
