package com.example.satellitetracker.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.satellitetracker.R
import com.example.satellitetracker.domain.model.Position
import com.example.satellitetracker.domain.model.SatelliteDetail
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    showSnackBar: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState(initial = DetailUiState())

    LaunchedEffect(key1 = viewModel) {
        with(viewModel) { setEvent(DetailEvent.LoadSatelliteDetail) }
    }

    LaunchedEffect(key1 = viewModel.effect) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is DetailEffect.ShowError -> {
                    showSnackBar(effect.message)
                }
            }
        }.collect {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.satelliteDetail?.let {
                            stringResource(id = R.string.detail_title)
                        } ?: stringResource(id = R.string.loading)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.error!!)
                    }
                }

                uiState.satelliteDetail != null -> {
                    SatelliteDetailContent(
                        detail = uiState.satelliteDetail!!,
                        position = uiState.currentPosition
                    )
                }
            }
        }
    }
}

@Composable
fun SatelliteDetailContent(detail: SatelliteDetail, position: Position?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DetailRow(
                    label = stringResource(id = R.string.first_flight),
                    value = detail.firstFlight
                )
                HorizontalDivider()
                DetailRow(
                    label = stringResource(id = R.string.height_mass),
                    value = "${detail.height}/${detail.mass}"
                )
                HorizontalDivider()
                DetailRow(
                    label = stringResource(id = R.string.cost),
                    value = detail.costPerLaunch.toString()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(id = R.string.last_position),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        if (position != null) {
            Text("(${position.posX}, ${position.posY})")
        } else {
            Text(stringResource(id = R.string.loading_position))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}
