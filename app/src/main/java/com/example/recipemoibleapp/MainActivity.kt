package com.example.recipemoibleapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import client
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable
import com.example.recipemoibleapp.ui.theme.RecipeMoibleAppTheme
import io.ktor.client.call.body
import io.ktor.client.request.get

val SalmonRed = Color(0xFFD96868)
val OffWhite = Color(0xFFE6E4E2)
val SageGreen = Color(0xFF7B8E4D)
val DarkForestGreen = Color(0xFF536136)
val LogoBackground = Color(0xFFF4F0E5)

@Serializable // Add this annotation!
data class Recipe(
    val id: String, // phpMyAdmin usually uses Int IDs
    val foodName: String,
    val foodType: String,
    val author: String,
    val imageUrl: String,
    val isFavorite: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeMoibleAppTheme {
                // Simply call your navigation wrapper
                AppNavigation()
            }
        }
    }
}
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToSignUp: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LogoBackground), // Set the background to match your logo
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo Image
            Image(
                painter = painterResource(id = R.drawable.make_it_logo_nobg),
                contentDescription = "Make It Logo",

                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(300.dp)
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .clip(RectangleShape)
            )

            // Container for input fields
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = OffWhite) // Keep the container color unaffected
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DarkForestGreen
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { onLoginSuccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                    ) {
                        Text("Login", color = Color.White)
                    }

                    TextButton(onClick = { onNavigateToSignUp() }) {
                        Text("Don't have an account? Sign Up", color = SalmonRed)
                    }
                }
            }
        }
    }
}
@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onBackToLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Using a coroutine scope to trigger the Ktor network call
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LogoBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo Image
            Image(
                painter = painterResource(id = R.drawable.make_it_logo_nobg),
                contentDescription = "Make It Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(300.dp)
                    .height(120.dp)
                    .padding(bottom = 10.dp)
                    .clip(RectangleShape)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = OffWhite)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DarkForestGreen
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            // This is where you'll call your Ktor POST request
                            // For now, it just triggers the success navigation
                            onSignUpSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                    ) {
                        Text("Sign Up", color = Color.White)
                    }

                    // Optional: A small "Back" button if they clicked Sign Up by mistake
                    TextButton(onClick = onBackToLogin) {
                        Text("Back to Login", color = DarkForestGreen)
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) { // Added navController parameter
    val recipeList = listOf(
        Recipe("1", "Chicken Adobo", "Meat, Savory", "user123", "https://example.com/adobo.jpg"),
        Recipe("2", "Beef Sinigang", "Soup, Sour", "chef_mcl", "https://example.com/sinigang.jpg"),
        Recipe("3", "Pork Lumpia", "Appetizer, Fried", "lola_cooks", "https://example.com/lumpia.jpg")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Make IT", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkForestGreen)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_recipe") },
                containerColor = SalmonRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        containerColor = OffWhite
    ) { padding ->
        // LazyColumn is efficient for long lists
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Welcome to your Kitchen",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DarkForestGreen,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Ensure 'recipeList' is defined before this block
            items(recipeList) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onFavoriteClick = { /* Handle favorite */ },
                    onCardClick = { /* Handle navigation */ }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(onCancel: () -> Unit, onPost: (Recipe) -> Unit) {
    var name by remember { mutableStateOf("") }
    var foodType by remember { mutableStateOf("") }

    // Lists to hold the added items
    val ingredients = remember { mutableStateListOf<String>() }
    val steps = remember { mutableStateListOf<String>() }

    // State to hold the URI of the selected image
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // This is the "Launcher" that opens the gallery
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Modal Control States
    var showIngredientModal by remember { mutableStateOf(false) }
    var showStepModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Recipe", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DarkForestGreen)
            )
        },
        containerColor = OffWhite
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name of Dish") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = foodType, onValueChange = { foodType = it }, label = { Text("Type of Food") }, modifier = Modifier.fillMaxWidth())
            }

            // Ingredients Section
            item {
                Text("Ingredients", fontWeight = FontWeight.Bold, color = DarkForestGreen)
            }
            items(ingredients) { ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("• $ingredient", modifier = Modifier.weight(1f))
                    IconButton(onClick = { ingredients.remove(ingredient) }) {
                        Icon(
                            imageVector = Icons.Default.Delete, // Make sure to import Icons.Default.Delete
                            contentDescription = "Delete",
                            tint = SalmonRed
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = { showIngredientModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("+ Add Ingredient")
                }
            }

// Steps Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Steps", fontWeight = FontWeight.Bold, color = DarkForestGreen)
            }
            itemsIndexed(steps) { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}. $step", modifier = Modifier.weight(1f))
                    IconButton(onClick = { steps.removeAt(index) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = SalmonRed
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = { showStepModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("+ Add a Step")
                }
            }

            // Image Upload Placeholder
            item {
                Text("Recipe Photo", fontWeight = FontWeight.Bold, color = DarkForestGreen)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { imagePicker.launch("image/*") }, // Triggers the picker
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (selectedImageUri != null) {
                            // Display the selected image using AsyncImage (Coil)
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = DarkForestGreen)
                                Text("Tap to upload photo", color = DarkForestGreen)
                            }
                        }
                    }
                }
            }

            // Bottom Actions
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel", color = SalmonRed) }
                    Button(onClick = { /* TODO: Ktor Post Logic */ }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = SalmonRed)) {
                        Text("Post")
                    }
                }
            }
        }
    }

    // Modal Logic
    if (showIngredientModal) {
        InputModal(
            title = "Add Ingredient",
            onDismiss = { showIngredientModal = false },
            onAdd = { ingredients.add(it) }
        )
    }

    if (showStepModal) {
        InputModal(
            title = "Add Step",
            onDismiss = { showStepModal = false },
            onAdd = { steps.add(it) }
        )
    }
}
@Composable
fun RecipeCard(
    recipe: Recipe,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // IMAGE SECTION
            // Using Coil's AsyncImage (Make sure you have the Coil dependency!)
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.foodName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFE0E0E0)),
                contentScale = ContentScale.Crop,
                // Replace R.drawable.foodph with your actual placeholder filename
                placeholder = painterResource(id = R.drawable.foodph),
                error = painterResource(id = R.drawable.foodph)
            )

            // TEXT CONTENT AND FAVORITE ICON
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.foodName, // Match your data class 'foodName'
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Text(
                        text = recipe.foodType, // Match your data class 'foodType'
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "By: ${recipe.author}", // Match your data class 'author'
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = if (recipe.isFavorite) SalmonRed else Color.DarkGray
                    )
                }
            }
        }
    }
}
@Composable
fun InputModal(
    title: String,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = DarkForestGreen,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Type here...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // "Add" button placed right under the text field for quick thumb access
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        if (textInput.isNotBlank()) {
                            onAdd(textInput)
                            textInput = "" // Clear for next input
                        }
                    }) {
                        Text("Add to List", color = SageGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            // The main "Done" button that exits the modal
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Done", color = Color.White)
            }
        }
    )
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onNavigateToSignUp = { navController.navigate("signup") } // Add this!
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    // Usually, you'd go to home or back to login after registering
                    navController.navigate("login")
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable("create_recipe") {
            CreateRecipeScreen(
                onCancel = { navController.popBackStack() },
                onPost = { /* Handle Ktor Post */ }
            )
        }

        composable("home") {
            HomeScreen(navController = navController)
        }
    }
}
suspend fun getRecipes(): List<Recipe> {
    // Replace 10.0.2.2 with your PC's IP if using a real phone
    return client.get("http://10.0.2.2:3000/api/recipes").body()
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    RecipeMoibleAppTheme {
        LoginScreen(onLoginSuccess = {}, onNavigateToSignUp = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    RecipeMoibleAppTheme {
        SignUpScreen(onSignUpSuccess = {}, onBackToLogin = {})
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    RecipeMoibleAppTheme {
        // Use a dummy controller for the preview
        HomeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateRecipeScreenPreview() {
    RecipeMoibleAppTheme {
        // We pass empty lambdas for onCancel and onPost for the preview
        CreateRecipeScreen(
            onCancel = {},
            onPost = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeCardPreview() {
    RecipeMoibleAppTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(OffWhite)
        ) {
            RecipeCard(
                recipe = Recipe(
                    id = "1",
                    foodName = "Classic Beef Adobo",
                    foodType = "Meat, Savory",
                    author = "chef_mcl",
                    imageUrl = ""
                ),
                onFavoriteClick = {},
                onCardClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecipeCard(
                recipe = Recipe(
                    id = "2",
                    foodName = "Sinigang na Baboy",
                    foodType = "Soup, Sour",
                    author = "lola_cooks",
                    imageUrl = "",
                    isFavorite = true
                ),
                onFavoriteClick = {},
                onCardClick = {}
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun IngredientModalPreview() {
    RecipeMoibleAppTheme {
        // Previewing the Ingredient version
        InputModal(
            title = "Add Ingredient",

            onDismiss = {},
            onAdd = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StepModalPreview() {
    RecipeMoibleAppTheme {
        // Previewing the Step version (which has the 'Done' button)
        InputModal(
            title = "Add Step",

            onDismiss = {},
            onAdd = {}
        )
    }
}