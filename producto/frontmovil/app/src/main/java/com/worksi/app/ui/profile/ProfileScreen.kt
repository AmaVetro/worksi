package com.worksi.app.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import com.worksi.app.ui.components.CandidateMainBottomBar
import com.worksi.app.ui.components.CvViewerOverlay
import com.worksi.app.ui.components.CandidateTopSessionActions
import com.worksi.app.ui.components.MainTab
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.White
import com.worksi.app.validation.PdfCvTextRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MAX_CV_BYTES = 1_000_000L

private val PageBackground = Color(0xFFF8F8F8)
private val ChipBackground = Color(0xFFE3F2FD)
private val ChipText = Color(0xFF1565C0)
private val MutedText = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSaved: () -> Unit = {},
    onNavigateToApplications: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onLogout: () -> Unit
) {
    val state by viewModel.ui.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pickValidating by remember { mutableStateOf(false) }

    val saveCvLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
            val bytes = state.cvPdfBytes ?: return@rememberLauncherForActivityResult
            if (uri == null) return@rememberLauncherForActivityResult
            context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
        }

    val pickCvLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri == null) return@rememberLauncherForActivityResult
            val resolver = context.contentResolver
            val mime = resolver.getType(uri)
            if (mime != null && mime != "application/pdf") {
                return@rememberLauncherForActivityResult
            }
            val size = resolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: 0L
            if (size <= 0L || size > MAX_CV_BYTES) {
                return@rememberLauncherForActivityResult
            }
            var display: String? = null
            resolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { c ->
                    if (c.moveToFirst()) {
                        val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (idx >= 0) display = c.getString(idx)
                    }
                }
            scope.launch {
                pickValidating = true
                val outcome =
                    withContext(Dispatchers.IO) {
                        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                        if (bytes == null) {
                            "No se pudo leer el archivo"
                        } else {
                            val err = PdfCvTextRules.validatePdfBytes(bytes)
                            if (err != null) {
                                err
                            } else {
                                Pair(bytes, display ?: "cv.pdf")
                            }
                        }
                    }
                pickValidating = false
                when (outcome) {
                    is String -> viewModel.setCvModalError(outcome)
                    is Pair<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        val pair = outcome as Pair<ByteArray, String>
                        viewModel.uploadCv(pair.first, pair.second)
                    }
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", color = White) },
                actions = {
                    CandidateTopSessionActions(
                        onNavigateToMatches = onNavigateToMatches,
                        onLogout = onLogout)
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = CyanPrimary))
        },
        bottomBar = {
            CandidateMainBottomBar(
                selected = MainTab.Profile,
                onProfile = { },
                onOffers = onNavigateToHome,
                onSaved = onNavigateToSaved,
                onApplications = onNavigateToApplications)
        }) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(PageBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = White),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = ButtonDefaults.ContentPadding) {
                    Text("Volver", fontSize = 14.sp)
                }
                Text(
                    text = "Perfil",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanPrimary)
            }

            Spacer(Modifier.height(20.dp))

            when {
                state.isLoading ->
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CyanPrimary)
                    }
                state.errorMessage != null -> {
                    Text(state.errorMessage ?: "", color = Color(0xFFB00020), fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.reload() }) { Text("Reintentar") }
                }
                else -> {
                    PersonalInfoBlock(
                        fullName = state.fullName,
                        sectorLine = state.sectorLine,
                        locationLine = state.locationLine,
                        email = state.email,
                        phone = state.phone,
                        onEdit = { viewModel.openEditSection(ProfileEditSection.PERSONAL) })

                    Spacer(Modifier.height(20.dp))

                    SectionWithEditTitle(
                        title = "Descripción",
                        onEdit = { viewModel.openEditSection(ProfileEditSection.DESCRIPTION) }) {
                        Text(
                            text = state.description,
                            fontSize = 15.sp,
                            color = Color.Black,
                            lineHeight = 22.sp,
                            modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = { viewModel.openCvModal() },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, CyanPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Tu CV", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(8.dp))
                            Icon(
                                Icons.Filled.Description,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = CyanPrimary)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    SalaryBlock(
                        salaryLine = state.salaryLine,
                        onEdit = { viewModel.openEditSection(ProfileEditSection.SALARY) })

                    Spacer(Modifier.height(20.dp))

                    YearsExperienceBlock(
                        yearsLine = state.yearsExperienceLine,
                        onEdit = { viewModel.openEditSection(ProfileEditSection.YEARS) })

                    Spacer(Modifier.height(20.dp))

                    PreferenceBlock(
                        title = "Modalidades preferidas",
                        chips = state.modalities,
                        onEdit = { viewModel.openEditSection(ProfileEditSection.MODALITIES) })

                    Spacer(Modifier.height(16.dp))

                    PreferenceBlock(
                        title = "Cargas horarias preferidas",
                        chips = state.workloads,
                        onEdit = { viewModel.openEditSection(ProfileEditSection.WORKLOADS) })

                    Spacer(Modifier.height(20.dp))

                    SkillsBlock(
                        skillNames = state.skills.map { it.name },
                        onEdit = { viewModel.openEditSection(ProfileEditSection.SKILLS) })

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    ProfileEditModalsHost(
        section = state.activeEditSection,
        state = state,
        onRegionChange = viewModel::onEditRegionSelected,
        onCancel = viewModel::closeEditSection,
        onSave = viewModel::saveProfilePatch)

    CvViewerOverlay(
        visible = state.cvModalVisible,
        loading = state.cvModalLoading || pickValidating,
        errorMessage = state.cvModalError,
        pdfBytes = state.cvPdfBytes,
        onClose = { viewModel.closeCvModal() },
        onDownload = { saveCvLauncher.launch(state.cvFilename) },
        onChangeCv = { pickCvLauncher.launch(arrayOf("application/pdf")) },
        uploadBusy = state.cvUploading)
}

@Composable
private fun PersonalInfoBlock(
    fullName: String,
    sectorLine: String,
    locationLine: String,
    email: String,
    phone: String,
    onEdit: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fullName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Rubro: $sectorLine",
                fontSize = 15.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(4.dp))
            Text(
                text = locationLine,
                fontSize = 14.sp,
                color = MutedText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(email, fontSize = 13.sp, color = MutedText)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Phone, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(phone, fontSize = 13.sp, color = MutedText)
                }
            }
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun SectionWithEditTitle(title: String, onEdit: () -> Unit, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
        }
    }
    Spacer(Modifier.height(8.dp))
    content()
}

@Composable
private fun SalaryBlock(salaryLine: String, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sueldo esperado", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
            Spacer(Modifier.height(6.dp))
            Text(salaryLine, fontSize = 15.sp, color = Color.Black, textAlign = TextAlign.Center)
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun YearsExperienceBlock(yearsLine: String, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Experiencia laboral", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
            Spacer(Modifier.height(6.dp))
            Text(yearsLine, fontSize = 15.sp, color = Color.Black, textAlign = TextAlign.Center)
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreferenceBlock(title: String, chips: List<String>, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CyanPrimary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center)
        IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
        }
    }
    Spacer(Modifier.height(10.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (chips.isEmpty()) {
            Text("—", color = MutedText, fontSize = 14.sp)
        } else {
            chips.forEach { label -> ProfileChip(label) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillsBlock(skillNames: List<String>, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Skills", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(22.dp))
        }
    }
    Spacer(Modifier.height(10.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (skillNames.isEmpty()) {
            Text("—", color = MutedText, fontSize = 14.sp)
        } else {
            skillNames.forEach { ProfileChip(it) }
        }
    }
}

@Composable
private fun ProfileChip(label: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ChipBackground,
        border = BorderStroke(1.dp, ChipText)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            color = ChipText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium)
    }
}
