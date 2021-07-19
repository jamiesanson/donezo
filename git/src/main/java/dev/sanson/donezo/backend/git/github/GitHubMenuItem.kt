package dev.sanson.donezo.backend.git.github

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sanson.donezo.backend.BackendMenuItem
import dev.sanson.donezo.backend.git.R

object GitHubMenuItem : BackendMenuItem {

    @Composable
    override fun Icon() {
        Icon(
            painter = painterResource(id = R.drawable.ic_github),
            contentDescription = "GitHub logo",
            modifier = Modifier.size(32.dp)
        )
    }

    override val title: String
        get() = "GitHub"

    override val description: String
        get() = "Sync your to do items with a git repository on GitHub"
}
