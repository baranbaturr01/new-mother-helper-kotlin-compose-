package com.baranbatur.newmotherhelper.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.baranbatur.newmotherhelper.components.BottomNavigationBarWtihAds
import com.baranbatur.newmotherhelper.service.BlogData
import com.baranbatur.newmotherhelper.service.BlogResponse
import com.baranbatur.newmotherhelper.service.CommentsData
import com.baranbatur.newmotherhelper.service.RetrofitClient
import com.baranbatur.newmotherhelper.ui.theme.WhiteColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen(navController: NavController) {
    var items by remember { mutableStateOf<List<BlogData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val sharedPreferences2 = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences2.getString("token", "") ?: ""
    var expandedItemId by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        RetrofitClient.instance.getBlogPosts("Bearer $token")
            .enqueue(object : Callback<BlogResponse> {
                override fun onResponse(
                    call: Call<BlogResponse>, response: Response<BlogResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        items = response.body()?.data ?: emptyList()
                    } else {
                        Toast.makeText(context, "Failed to load blog posts", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<BlogResponse>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                Toast.makeText(context, "Yeni Post Ekle", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Yeni Post Ekle")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Postlar", color = WhiteColor, fontSize = 24.sp) },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary)
            )
        },
        bottomBar = {
            BottomNavigationBarWtihAds(navController = navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                        .padding(16.dp)
                )
            } else if (items.isEmpty()) {
                EmptyPage()
            } else {
                LazyColumn(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { blogItem ->
                        BlogItemCard(
                            item = blogItem, isExpanded = expandedItemId == blogItem.id, onClick = {
                                expandedItemId =
                                    if (expandedItemId == blogItem.id) null else blogItem.id
                            }, comments = blogItem.comments
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BlogItemCard(
    item: BlogData, isExpanded: Boolean, onClick: () -> Unit, comments: List<CommentsData>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(2.dp)
            .shadow(8.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User Info Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = Color(0xFFF5B0A7),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = item.user.name,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = item.createdAt.toString().substring(0, 10),
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }

            // Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Content
            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Like button and comment count at the bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(45.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* Handle like action */ }) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Like",
                            tint = Color(0xFFF5B0A7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "${item.voteCount}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Comments",
                        tint = Color(0xFFF5B0A7),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${item.commentCount} Yorum",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Expanded Comments Section
            if (isExpanded) {
                if (comments.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                        items(comments.take(5)) { comment ->
                            CommentRow(comment = comment)
                        }
                    }
                } else {
                    Text(text = "Yorum yok.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun CommentRow(comment: CommentsData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = "${comment.user.name} ${comment.user.surname}:",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
        )
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )
    }
}
