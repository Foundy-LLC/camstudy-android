package io.foundy.setting.ui.organization

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.OrganizationOverview
import io.foundy.setting.ui.R
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun OrganizationEditRoute(
    // TODO: resultRecipient로 변경하기
    navigator: DestinationsNavigator,
    viewModel: OrganizationEditViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    // TODO: 소속 이름, 이메일 입력한 경우 뒤로가기 눌렀을 때 다시 묻기

    OrganizationEditScreen(
        uiState = uiState,
        popBackStack = {
            navigator.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationEditScreen(
    uiState: OrganizationEditUiState,
    popBackStack: () -> Unit
) {
    Scaffold(
        topBar = {
            CamstudyTopAppBar(
                title = {
                    CamstudyText(text = stringResource(id = R.string.organization_edit))
                },
                onBackClick = popBackStack
            )
        },
        // TODO: CamstudyScaffold 만들어서 중복되는 색상 코드 제거하기
        containerColor = CamstudyTheme.colorScheme.systemUi01
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (uiState) {
                is OrganizationEditUiState.Failure -> TODO()
                OrganizationEditUiState.Loading -> Loading()
                is OrganizationEditUiState.Success -> Success(uiState = uiState)
            }
        }
    }
}

@Composable
private fun Loading() {
    // TODO
}

@Composable
private fun Success(uiState: OrganizationEditUiState.Success) {
    var organizationToDelete by remember { mutableStateOf<OrganizationOverview?>(null) }

    organizationToDelete?.let { organization ->
        CamstudyDialog(
            content = stringResource(
                R.string.remove_organization_recheck_content,
                organization.name
            ),
            onDismissRequest = { organizationToDelete = null },
            onCancel = { organizationToDelete = null },
            onConfirm = {
                uiState.onDeleteClick(organization)
                organizationToDelete = null
            },
            confirmText = stringResource(R.string.remove_organization_confirm_button)
        )
    }

    LazyColumn {
        item {
            MyOrganizations(
                organizations = uiState.registeredOrganizations,
                onDeleteClick = { organizationToDelete = it }
            )
        }
    }
}

@Composable
private fun MyOrganizations(
    organizations: List<OrganizationOverview>,
    onDeleteClick: (OrganizationOverview) -> Unit
) {
    Column(
        modifier = Modifier
            .heightIn(min = 140.dp)
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .padding(vertical = 20.dp)
    ) {
        if (organizations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                CamstudyText(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.empty_registered_organization),
                    style = CamstudyTheme.typography.titleLarge.copy(
                        color = CamstudyTheme.colorScheme.systemUi04,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        } else {
            CamstudyText(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.registered_organization_title),
                style = CamstudyTheme.typography.titleSmall.copy(
                    color = CamstudyTheme.colorScheme.systemUi07,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            OrganizationChipFlowRow(
                modifier = Modifier.padding(4.dp),
                organizations = organizations,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OrganizationChipFlowRow(
    modifier: Modifier = Modifier,
    organizations: List<OrganizationOverview>,
    onDeleteClick: (OrganizationOverview) -> Unit
) {
    FlowRow(modifier = modifier) {
        for (organization in organizations) {
            OrganizationChip(
                organization = organization,
                onDeleteClick = { onDeleteClick(organization) }
            )
        }
    }
}

@Composable
private fun OrganizationChip(
    organization: OrganizationOverview,
    onDeleteClick: () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = CamstudyTheme.colorScheme.primary.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            CamstudyText(
                text = organization.name,
                style = CamstudyTheme.typography.titleSmall.copy(
                    color = CamstudyTheme.colorScheme.primaryPress,
                    fontWeight = FontWeight.Normal
                )
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    onClick = onDeleteClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = CamstudyTheme.colorScheme.systemUi06)
                .align(Alignment.TopEnd)
        ) {
            CamstudyIcon(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.Center),
                tint = CamstudyTheme.colorScheme.systemBackground,
                icon = CamstudyIcons.Close,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, backgroundColor = 0xff000000)
@Composable
private fun OrganizationChipPreview() {
    CamstudyTheme {
        OrganizationChip(
            organization = OrganizationOverview(id = "id", name = "한성대학교"),
            onDeleteClick = {}
        )
    }
}

@Preview
@Composable
private fun OrganizationChipFlowRowPreview() {
    CamstudyTheme {
        OrganizationChipFlowRow(
            organizations = listOf(
                OrganizationOverview(id = "id", name = "한성대학교"),
                OrganizationOverview(id = "id", name = "대단한대학교"),
                OrganizationOverview(id = "id", name = "엄청나는대학교"),
                OrganizationOverview(id = "id", name = "한성대학교"),
                OrganizationOverview(id = "id", name = "구글"),
                OrganizationOverview(id = "id2", name = "서울대학교")
            ),
            onDeleteClick = {}
        )
    }
}
