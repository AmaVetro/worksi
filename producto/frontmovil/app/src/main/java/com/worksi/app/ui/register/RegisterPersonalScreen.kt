package com.worksi.app.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import com.worksi.app.validation.PasswordPolicy
import com.worksi.app.validation.RutRules

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val RegisterBackButtonRowHeight = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPersonalScreen(
    viewModel: CandidateRegisterViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state by viewModel.ui.collectAsState()
    val d = state.draft
    var showErrors by remember { mutableStateOf(false) }
    var regionMenu by remember { mutableStateOf(false) }
    var communeMenu by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadRegions()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyanPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, top = RegisterBackButtonRowHeight, end = 24.dp, bottom = 24.dp)
        ) {
            Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall, color = White)
            Spacer(Modifier.height(16.dp))

            if (state.catalogLoading && state.regions.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = White
                )
                Spacer(Modifier.height(16.dp))
            }

            state.catalogError?.let { err ->
                Text(err, color = OrangeAccent)
                Spacer(Modifier.height(8.dp))
            }

            regField("Nombre (*)", d.firstName, { s -> viewModel.updateDraft { it.copy(firstName = s) } }, showErrors && d.firstName.isBlank())
            regField("Segundo nombre", d.middleName, { s -> viewModel.updateDraft { it.copy(middleName = s) } }, false)
            regField("Apellido paterno (*)", d.lastNamePaternal, { s -> viewModel.updateDraft { it.copy(lastNamePaternal = s) } }, showErrors && d.lastNamePaternal.isBlank())
            regField("Apellido materno (*)", d.lastNameMaternal, { s -> viewModel.updateDraft { it.copy(lastNameMaternal = s) } }, showErrors && d.lastNameMaternal.isBlank())
            regField("Correo (*)", d.email, { s -> viewModel.updateDraft { it.copy(email = s) } }, showErrors && (d.email.isBlank() || !EMAIL_REGEX.matches(d.email.trim())))
            RegisterOutlinedTextField(
                value = d.password,
                onValueChange = { s -> viewModel.updateDraft { it.copy(password = s) } },
                label = { Text("Contrasena (*)") },
                visualTransformation = PasswordVisualTransformation(),
                isError = showErrors && !PasswordPolicy.matches(d.password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            regField("Celular (*)", d.phone, { s -> viewModel.updateDraft { it.copy(phone = s) } }, showErrors && d.phone.isBlank())
            regField("RUT (*)", d.rut, { s -> viewModel.updateDraft { it.copy(rut = s) } }, showErrors && !RutRules.isValidChileRut(d.rut))
            regField("Nro. documento (*)", d.documentNumber, { s -> viewModel.updateDraft { it.copy(documentNumber = s) } }, showErrors && d.documentNumber.isBlank())
            regField("Calle (opcional)", d.street, { s -> viewModel.updateDraft { it.copy(street = s) } }, false, singleLine = false)

            Spacer(Modifier.height(8.dp))
            Text("Region (*)", color = White.copy(alpha = 0.9f))
            ExposedDropdownMenuBox(
                expanded = regionMenu,
                onExpandedChange = { regionMenu = !regionMenu }
            ) {
                RegisterOutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = state.regions.find { it.id == d.regionId }?.name ?: "",
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionMenu) },
                    isError = showErrors && d.regionId == null,
                    label = { Text("Selecciona region") }
                )
                DropdownMenu(
                    expanded = regionMenu,
                    onDismissRequest = { regionMenu = false }
                ) {
                    state.regions.forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r.name) },
                            onClick = {
                                viewModel.onRegionSelected(r.id)
                                regionMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Comuna (*)", color = White.copy(alpha = 0.9f))
            ExposedDropdownMenuBox(
                expanded = communeMenu,
                onExpandedChange = { if (d.regionId != null) communeMenu = !communeMenu }
            ) {
                RegisterOutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    enabled = d.regionId != null,
                    value = state.communes.find { it.id == d.communeId }?.name ?: "",
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = communeMenu) },
                    isError = showErrors && d.communeId == null,
                    label = { Text("Selecciona comuna") }
                )
                DropdownMenu(
                    expanded = communeMenu,
                    onDismissRequest = { communeMenu = false }
                ) {
                    state.communes.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.name) },
                            onClick = {
                                viewModel.updateDraft { it.copy(communeId = c.id) }
                                communeMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            RegisterRequiredFieldsHint(showErrors && !d.isPersonalStepValid())
            Button(
                onClick = {
                    showErrors = true
                    viewModel.clearCatalogError()
                    if (d.isPersonalStepValid()) onNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent, contentColor = White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Siguiente", fontWeight = FontWeight.Bold)
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = White)
        }
    }
}

@Composable
private fun regField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    isError: Boolean,
    singleLine: Boolean = true
) {
    Column {
        RegisterOutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text(label) },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine
        )
        Spacer(Modifier.height(8.dp))
    }
}

private fun RegisterDraft.isPersonalStepValid(): Boolean =
    firstName.isNotBlank() &&
        lastNamePaternal.isNotBlank() &&
        lastNameMaternal.isNotBlank() &&
        email.isNotBlank() &&
        EMAIL_REGEX.matches(email.trim()) &&
        PasswordPolicy.matches(password) &&
        phone.isNotBlank() &&
        RutRules.isValidChileRut(rut) &&
        documentNumber.isNotBlank() &&
        regionId != null &&
        communeId != null
