package com.example.recipemoibleapp

import android.content.Context
import android.net.Uri
import io.ktor.client.engine.cio.*
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable
import com.example.recipemoibleapp.ui.theme.RecipeMoibleAppTheme
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import org.json.JSONObject
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.compose.currentBackStackEntryAsState
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData


val SalmonRed = Color(0xFFD96868)
val OffWhite = Color(0xFFE6E4E2)
val SageGreen = Color(0xFF7B8E4D)
val DarkForestGreen = Color(0xFF536136)
val LogoBackground = Color(0xFFF4F0E5)


@Serializable
data class Recipe(
    val foodName: String,
    val foodType: String,
    val authorId: String,
    val imgUrl: String,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val difficulty: String,
    val isFavorite: Boolean = false
)

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    NavItem("Profile", Icons.Default.Person, "profile"),
    NavItem("Home", Icons.Default.Home, "home"),
    NavItem("Search", Icons.Default.Search, "search"),
    NavItem("Favorites", Icons.Default.Favorite, "favorites"),
    NavItem("My Recipes", Icons.Default.List, "user_recipes") // New Item
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
    val mContext = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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
                        onClick = {
                            scope.launch {
                                KTOR_Login(
                                    context = mContext,
                                    username = username,
                                    password = password,
                                    onLoginSuccess = {
                                        onLoginSuccess()
                                    }
                                )
                            }
                        },
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
    val mContext = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                            scope.launch {
                                KTOR_SignUp(mContext, username, password)
                            }
                            onSignUpSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                    ) {
                        Text("Sign Up", color = Color.White)
                    }

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
fun HomeScreen(navController: NavController) { // Added navController parameter\
    var recipeList by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    val scope = rememberCoroutineScope()
//    val recipeList = listOf(
//        Recipe(
//            foodName = "Chicken Adobo",
//            foodType = "Meat, Savory",
//            authorId = "user123",
//            imgUrl = "https://example.com/adobo.jpg",
//            title = "Classic Chicken Adobo",
//            description = "A savory Filipino dish made with chicken braised in soy sauce and vinegar.",
//            ingredients = listOf("Chicken", "Soy Sauce", "Vinegar", "Garlic", "Bay Leaves"),
//            instructions = listOf("Marinate chicken", "Simmer until tender", "Serve with rice"),
//            difficulty = "easy",
//            isFavorite = false
//        ),
//        Recipe(
//            foodName = "Beef Sinigang",
//            foodType = "Soup, Sour",
//            authorId = "chef_mcl",
//            imgUrl = "https://example.com/sinigang.jpg",
//            title = "Beef Sinigang",
//            description = "A sour tamarind-based soup with beef and vegetables.",
//            ingredients = listOf("Beef", "Tamarind", "Kangkong", "Radish", "Tomatoes"),
//            instructions = listOf("Boil beef until tender", "Add tamarind and vegetables", "Simmer and serve hot"),
//            difficulty = "medium",
//            isFavorite = false
//        ),
//        Recipe(
//            foodName = "Pork Lumpia",
//            foodType = "Appetizer, Fried",
//            authorId = "lola_cooks",
//            imgUrl = "https://example.com/lumpia.jpg",
//            title = "Crispy Pork Lumpia",
//            description = "Fried spring rolls filled with seasoned pork and vegetables.",
//            ingredients = listOf("Ground Pork", "Carrots", "Cabbage", "Spring Roll Wrappers"),
//            instructions = listOf("Prepare filling", "Wrap in lumpia wrappers", "Deep fry until golden"),
//            difficulty = "easy",
//            isFavorite = false
//        )
//    )

    LaunchedEffect(Unit) {
        recipeList = fetchAllRecipes()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {
        // Header
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(DarkForestGreen)
//                .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
//        ) {
//            Text("Make IT", color = Color.White, style = MaterialTheme.typography.titleLarge)
//        }
        TopHeader()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
            if (recipeList.isEmpty()) {
                item {
                    Text("Loading recipes...", color = Color.Gray)
                }
            }
            items(recipeList) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onFavoriteClick = { /* Toggle Star */ },
                    onCardClick = { /* Detail View */ }
                )
            }
            // Add extra space at the bottom so the floating nav doesn't cover the last card
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(currentAuthorId: Int,onCancel: () -> Unit, onPost: (Recipe) -> Unit) {
    val scope = rememberCoroutineScope()
    val mContext = LocalContext.current

    var name by remember { mutableStateOf("") }
    var foodType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("easy") }
    var authorId by remember { mutableStateOf("") }

    val ingredients = remember { mutableStateListOf<String>() }
    val steps = remember { mutableStateListOf<String>() }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    val difficulties = listOf("Easy", "Medium", "Hard")
    var expanded by remember { mutableStateOf(false) }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name of Dish") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = foodType,
                    onValueChange = { foodType = it },
                    label = { Text("Type of Dish") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
//                OutlinedTextField(
//                    value = authorId,
//                    onValueChange = { authorId = it },
//                    label = { Text("Author ID") },
//                    modifier = Modifier.fillMaxWidth()
//                )
            }

            // Difficulty section wrapped in item{}
            item {
                Column {
                    Text("Difficulty", fontWeight = FontWeight.Bold, color = DarkForestGreen)

                    difficulties.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { difficulty = option }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (difficulty == option),
                                onClick = { difficulty = option }
                            )
                            Text(option, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                        }
                    }
                }
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



            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel", color = SalmonRed) }
                    Button(
                        onClick = {
                            val recipe = Recipe(
                                foodName = name,
                                foodType = foodType, // collect from UI or set default
                                authorId = currentAuthorId.toString(),
                                imgUrl = selectedImageUri?.toString() ?: "", // ✅ safe fallback
                                title = name,
                                description = description,
                                ingredients = ingredients.toList(),
                                instructions = steps.toList(),
                                difficulty = difficulty,
                                isFavorite = false
                            )


                            scope.launch {
                                postRecipe(
                                    context = mContext,
                                    recipe = recipe,
                                    currentAuthorId = currentAuthorId,
                                    onSuccess = { onPost(recipe) },
                                    onError = { msg ->
                                        Toast.makeText(mContext, "Failed: $msg", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SalmonRed)
                    ) {
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
fun ProfileScreen(navController: NavController, userName: String) {
    // Wrap in a Column without extra padding at the top level
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {
        TopHeader() // This will now sit correctly at the top

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                color = DarkForestGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0D7F7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFF6750A4)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = userName, style = MaterialTheme.typography.headlineSmall, color = DarkForestGreen)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation Cards
            ProfileNavCard(
                title = "Favorites",
                subtitle = "Your liked recipes",
                onClick = {
                    navController.navigate("favorites") {
                        // This prevents creating a massive stack of screens
                        launchSingleTop = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileNavCard(
                title = "Recipes Created",
                subtitle = "Recipes you've shared",
                onClick = {
                    navController.navigate("user_recipes") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
@Composable
fun UserRecipesScreen(navController: NavController) {
    Scaffold(
        topBar = { TopHeader() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_recipe") },
                containerColor = SalmonRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Recipe")
            }
        },
        containerColor = OffWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "My Creations",
                style = MaterialTheme.typography.headlineSmall,
                color = DarkForestGreen,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // This is where you will eventually call a function
            // to load recipes where authorId == "Raphael"
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You haven't posted any recipes yet.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ProfileNavCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F0F7)), // Light grayish/purple
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder Icon Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEADDFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "A", color = Color(0xFF21005D))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
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
                model = recipe.imgUrl,
                contentDescription = recipe.foodName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFE0E0E0)),
                contentScale = ContentScale.Crop,
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
                        text = "By: ${recipe.authorId}", // Match your data class 'author'
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
fun FloatingBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp) // Creates the floating effect
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 10.dp,
            modifier = Modifier.clip(RoundedCornerShape(30.dp)) // Rounded edges
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    selected = isSelected,
                    alwaysShowLabel = false,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) SageGreen else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = if (isSelected) DarkForestGreen else Color.Gray,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TopHeader(title: String = "Make IT") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkForestGreen)
            .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge)
    }
}
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("home", "profile", "favorites", "search", "user_recipes")
    val sharedPref = remember { context.getSharedPreferences("UserSession", Context.MODE_PRIVATE) }
    val savedUserId = sharedPref.getInt("user_id", -1)
    val savedUsername = sharedPref.getString("username", "Guest") ?: "Guest"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FloatingBottomBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { navController.navigate("home") },
                    onNavigateToSignUp = { navController.navigate("signup") }
                )
            }
            composable("signup") {
                SignUpScreen(
                    onSignUpSuccess = { navController.navigate("login") },
                    onBackToLogin = { navController.popBackStack() }
                )
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("profile") {
                ProfileScreen(navController = navController, userName = savedUsername)
            }
            composable("create_recipe") {
                CreateRecipeScreen(
                    currentAuthorId = savedUserId,
                    onCancel = { navController.popBackStack() },
                    onPost = { navController.navigate("user_recipes") } // Navigate to your list after posting
                )
            }
            composable("user_recipes") {
                UserRecipesScreen(navController = navController)
            }
            composable("favorites") { /* Placeholder */ }
            composable("search") { /* Placeholder */ }
        }
    }
}



/*suspend fun getRecipes(): List<Recipe> {
    // Replace 10.0.2.2 with your PC's IP if using a real phone
    return client.get("http://10.0.2.2:3000/api/recipes").body<List<Recipe>>()
}*/


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
            currentAuthorId =1,
            onCancel = {},
            onPost = {}
        )
    }
}
@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    RecipeMoibleAppTheme {
        val navController = rememberNavController()
        // Mocking the screen inside a Column to simulate the background

            ProfileScreen(navController = navController, userName = "Raphael Correa")

    }
}
@Preview(showSystemUi = true)
@Composable
fun UserRecipesScreenPreview() {
    RecipeMoibleAppTheme {
        val navController = rememberNavController()
        UserRecipesScreen(navController = navController)
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
                    foodName = "Classic Beef Adobo",
                    foodType = "Meat, Savory",
                    authorId = "chef_mcl",
                    imgUrl = "",
                    title = "Classic Beef Adobo",
                    description = "A savory Filipino dish made with beef braised in soy sauce and vinegar.",
                    ingredients = listOf("Beef", "Soy Sauce", "Vinegar", "Garlic", "Bay Leaves"),
                    instructions = listOf("Marinate beef", "Simmer until tender", "Serve with rice"),
                    difficulty = "medium",
                    isFavorite = false
                ),
                onFavoriteClick = {},
                onCardClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecipeCard(
                recipe = Recipe(
                    foodName = "Sinigang na Baboy",
                    foodType = "Soup, Sour",
                    authorId = "lola_cooks",
                    imgUrl = "",
                    title = "Sinigang na Baboy",
                    description = "A sour tamarind-based soup with pork and vegetables.",
                    ingredients = listOf("Pork", "Tamarind", "Kangkong", "Radish", "Tomatoes"),
                    instructions = listOf("Boil pork until tender", "Add tamarind and vegetables", "Simmer and serve hot"),
                    difficulty = "easy",
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
fun FloatingBottomBarPreview() {
    RecipeMoibleAppTheme {
        val navController = rememberNavController()
        Box(modifier = Modifier.fillMaxSize().background(OffWhite)) {
            FloatingBottomBar(navController = navController)
        }
    }
}

const val BASE_URL = "10.0.2.2/REST"
suspend fun KTOR_SignUp(context: Context, username: String, password: String) {
    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
        }
    }
    try {
        // 1. CLEAN URL: No question marks or variables in the string
        val url = "http://$BASE_URL/sign_up.php"

        val response: HttpResponse = client.post(url) {
            // 2. SET CONTENT TYPE: Tells PHP to expect POST form data
            contentType(ContentType.Application.FormUrlEncoded)

            // 3. SEND BODY: This populates $_POST on the server
            setBody(FormDataContent(Parameters.build {
                append("username", username)
                append("password", password)
                append("role", "user")
            }))
        }

        val stringBody = response.bodyAsText()
        val json = JSONObject(stringBody)
        val status = json.optString("status")
        val message = json.optString("message") // Get the error message from PHP

        if (status == "success") {
            Toast.makeText(context, "Sign up successful!", Toast.LENGTH_SHORT).show()
        } else {
            // Show the actual message (e.g., "Username already taken")
            Toast.makeText(context, "Registration Failed: $message", Toast.LENGTH_SHORT).show()
        }

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        client.close()
    }
}


suspend fun KTOR_Login(
    context: Context,
    username: String,
    password: String,
    onLoginSuccess: () -> Unit
) {
    val client = HttpClient(CIO) {
        engine {
            // This is CRITICAL. Force HTTP 1.1 to match XAMPP's Apache
            https {
                // Not needed for http, but good to have engine-level config
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 20000
            connectTimeoutMillis = 20000
            socketTimeoutMillis = 20000
        }
    }
    try {
        val response: HttpResponse = client.post("http://$BASE_URL/login.php") {
            contentType(ContentType.Application.FormUrlEncoded)
            header("Connection", "close")
            header("Accept", "application/json")
            header("User-Agent", "Mozilla/5.0")
            setBody(FormDataContent(Parameters.build {
                append("username", username)
                append("password", password)
            }))
        }

        val stringBody = response.bodyAsText()
        println("Status: ${response.status}")
        println("Response: $stringBody")

        if (stringBody.isNotBlank()) {
            val json = JSONObject(stringBody)
            val status = json.optString("status")
            val message = json.optString("message")

            if (status == "success") {
                val role = json.optString("role")
                val userId = json.optInt("id") // Extract the ID from PHP
                val username = json.optString("username")

                // Save ID locally for "My Recipes" and "Favorites"
                val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                sharedPref.edit().apply {
                    putInt("user_id", userId)
                    putString("username", username)
                    apply()
                }

                Toast.makeText(context, "Welcome back, $username!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            } else {
                Toast.makeText(context, "Login failed: $message", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Empty response from server", Toast.LENGTH_SHORT).show()
        }

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        client.close()
    }
}


suspend fun postRecipe(
    context: Context,
    recipe: Recipe,
    currentAuthorId: Int,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val client = HttpClient(CIO)
    try {
        val imageUri = recipe.imgUrl.toUri()
        // Convert the URI into bytes for uploading
        val imageBytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "http://$BASE_URL/post_recipe.php",
            formData = formData {
                append("food_name", recipe.foodName)
                append("food_type", recipe.foodType)
                append("author_id", currentAuthorId.toString())
//                append("img_url", recipe.imgUrl)
                append("title", recipe.title)
                append("description", recipe.description)
                append("ingredients", recipe.ingredients.joinToString(","))
                append("instructions", recipe.instructions.joinToString(","))
                append("difficulty", recipe.difficulty.lowercase())
//                append("is_favorite", recipe.isFavorite.toString())

                if (imageBytes != null) {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"recipe_img.jpg\"")
                    })
                }
            }
        )

        val stringBody = response.bodyAsText()
        val json = JSONObject(stringBody)
        val status = json.optString("status")
        val message = json.optString("message")

        if (status == "success") {
            Toast.makeText(context, "Recipe posted!", Toast.LENGTH_SHORT).show()
            onSuccess()
        } else {
            onError(message)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onError("Network error: ${e.message}")
    } finally {
        client.close()
    }
}
suspend fun fetchAllRecipes(): List<Recipe> {
    val client = HttpClient(CIO)
    return try {
        // Replace with your actual endpoint for fetching all recipes
        val response: HttpResponse = client.get("http://$BASE_URL/get_all_recipes.php")
        val stringBody = response.bodyAsText()
        val jsonArray = org.json.JSONArray(stringBody)
        val recipes = mutableListOf<Recipe>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            recipes.add(
                Recipe(
                    foodName = item.getString("food_name"),
                    foodType = item.optString("food_type", ""),
                    authorId = item.getString("author_id"),
                    imgUrl = item.optString("img_url", ""),
                    title = item.optString("title", ""),
                    description = item.optString("description", ""),
                    // Splitting the comma-separated strings back into Lists
                    ingredients = item.optString("ingredients").split(",").filter { it.isNotBlank() },
                    instructions = item.optString("instructions").split(",").filter { it.isNotBlank() },
                    difficulty = item.optString("difficulty", "easy"),
                    isFavorite = false // Logic for favorites can be added later
                )
            )
        }
        recipes
    } catch (e: Exception) {
        emptyList()
    } finally {
        client.close()
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