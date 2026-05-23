package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LandingScreen(
    onNavigateToDashboard: () -> Unit,
    onSimulatePremiumBuy: (String) -> Unit
) {
    var demoJobTitle by remember { mutableStateOf("Senior Web Developer") }
    var pricingAnnual by remember { mutableStateOf(true) }
    var activeFaqIndex by remember { mutableStateOf(-1) }
    var showStripeCheckoutPlan by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("landing_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- HERO HEADER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("10,000+ Job Seekers in 50+ Countries", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Get More Interviews.\nBeat the ATS Filter.",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "An AI-powered resume builder and ATS score optimizer built to bypass automated hiring system blockers.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToDashboard,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("hero_cta_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Build Your Free Resume Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Credit Card Needed · 7-Day Guarantee", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                }
            }
        }

        // --- LIVE DEMO WIDGET (NO LOGIN REQUIRED) ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Live AI Demo (Try it!)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Type your target job title below to see an AI-tailored work achievement snippet generated in real-time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = demoJobTitle,
                        onValueChange = { demoJobTitle = it },
                        placeholder = { Text("e.g. Sales Executive") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Renders the live interactive snippet response
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Optimized STAR Achievments for '$demoJobTitle'", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val bullet1 = remember(demoJobTitle) {
                                getDemoBulletPoints(demoJobTitle)[0]
                            }
                            val bullet2 = remember(demoJobTitle) {
                                getDemoBulletPoints(demoJobTitle)[1]
                            }

                            Text(
                                text = "• $bullet1",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = "• $bullet2",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // --- CORE FEATURES GRID ---
        item {
            Column {
                Text(
                    "Beating the Filters, Earning Offer Letters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val features = listOf(
                    Triple(Icons.Default.Edit, "Live Visual Builder", "Left-side easy templates with Right-side pixel-perfect dynamic preview. Drag sections, custom fonts, colors."),
                    Triple(Icons.Default.AutoAwesome, "AI Resume Writer", "Uses Claude API to rewrite, optimize phrasing in verified STAR format & dynamically incorporate action verbs."),
                    Triple(Icons.Default.Troubleshoot, "ATS Score Analyzer", "Scores resume 0-100, detects missing keywords, highlights system parsers bugs, and allows One-Click auto-fixes!"),
                    Triple(Icons.Default.HistoryEdu, "AI Cover Letter Drawer", "Simultaneously generates 3 stylized letter drafts (Professional, Enthusiastic, Concise) tailored to target companies."),
                    Triple(Icons.Default.AssignmentTurnedIn, "Job Application Tracker", "Bespoke interview CRM log pipeline. Organize target jobs into columns and save application trackers.")
                )

                features.forEach { (icon, title, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), lineHeight = 16.sp)
                        }
                    }
                }
            }
        }

        // --- PRICING SECTION ---
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Transparent, Results-Driven Pricing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Invest in your career. Upgrade anytime.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!pricingAnnual) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { pricingAnnual = false }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Monthly", color = if (!pricingAnnual) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (pricingAnnual) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { pricingAnnual = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Annual", color = if (pricingAnnual) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFEA580C), CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("SAVE 45%", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FREE PLAN CARD
                PricingCard(
                    title = "Free Starter",
                    price = "$0",
                    period = "/forever",
                    features = listOf(
                        "Build up to 2 Resumes",
                        "3 AI-Powered ATS Checks",
                        "Watermarked PDF Export",
                        "Plain Text Copier Toolbar"
                    ),
                    buttonText = "Start Free Resume",
                    highlight = false,
                    onClick = onNavigateToDashboard
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PRO CARD
                PricingCard(
                    title = "ResumeAI Pro",
                    price = if (pricingAnnual) "$99" else "$14.99",
                    period = if (pricingAnnual) "/year" else "/month",
                    features = listOf(
                        "Unlimited Resume Documents",
                        "Unlimited ATS Score Checks",
                        "Un-watermarked High Quality PDF",
                        "Word (.docx) Export & Sharing Links",
                        "AI Cover Letter Drafter (Unlimited)",
                        "Complete Job Application CRM Tracker",
                        "Priority AI Queue Processing speeds"
                    ),
                    buttonText = "Become ResumeAI Pro",
                    highlight = true,
                    onClick = { showStripeCheckoutPlan = if (pricingAnnual) "pro_yearly" else "pro_monthly" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // LIFETIME ACCESS (LIMITED RUN EXCLUSIVE)
                PricingCard(
                    title = "Lifetime Founder Deal",
                    price = "$79",
                    period = " (one-time pay)",
                    features = listOf(
                        "All Pro features unlocked *forever*",
                        "Limited Launcher Deal",
                        "One-time payment, no subscriptions",
                        "7-day money-back guarantee protection"
                    ),
                    buttonText = "Secure Lifetime Account",
                    highlight = true,
                    gradient = true,
                    onClick = { showStripeCheckoutPlan = "pro_lifetime" }
                )
            }
        }

        // --- TESTIMONIALS (REALISTIC AVATARS INDEXED BY UI AVATARS) ---
        item {
            Column {
                Text(
                    "Success Stories From Global Hires",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val reviews = listOf(
                    Testimonial("Sarah L.", "Software Dev", "Amazon (US)", "ResumeAI boosted my keyword optimization score from 54 to 91 in minutes. Got an HR callback within 48 hours of my application! Best $15 I spent."),
                    Testimonial("Hans M.", "Product Mgr", "SAP (Germany)", "The live side-by-side ATS helper suggested crucial business metrics changes that dramatically changed my summary. Got the job!"),
                    Testimonial("Aarav P.", "Systems Arch", "Atlassian (Australia)", "The structural resume templates are genuinely readable by standard scanners. Checked with multiple job descriptors, highly visual scorebars!"),
                    Testimonial("Eliza G.", "Marketing lead", "L'Oreal (France)", "I drafted a tailored Enthusiastic cover letter that landed me an agency interview. The variants are extremely natural."),
                    Testimonial("David O.", "Finance Dir", "Barclays (UK)", "I bought the Lifetime deal on day 1. Beautiful professional layouts. I was able to manage and optimize four executive portfolio copies easily.")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    reviews.take(2).forEach { review ->
                        TestimonialWidget(review, Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    reviews.drop(2).take(2).forEach { review ->
                        TestimonialWidget(review, Modifier.weight(1f))
                    }
                }
            }
        }

        // --- FAQ SYSTEM ACCORDION (10 QUESTIONS) ---
        item {
            Column {
                Text(
                    "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val faqs = listOf(
                    "What is an Applicant Tracking System (ATS)?" to "ATS is software used by employers to screen and filter job applications. It scans resumes for specific keywords, job experience matches, and clean heading structures before ever forwarding to human eyes.",
                    "How does the ResumeAI Score Checker work?" to "Our checker scans your compiled resume and evaluates it against job description keywords via Gemini AI. It identifies formatting bugs, counts matches, and generates score percentages.",
                    "Does ResumeAI guarantee I bypass the ATS?" to "While no tool guarantees interviews, matching candidate keywords naturally, formatting in a single column style, and clarifying professional achievements drastically increases success rates.",
                    "Why must I avoid dual columns or tables?" to "Most basic parsing engines (especially older ones) read across horizontally. Dual columns or floating boxes can cause blocks of text to get jumbled together, corrupting readability data.",
                    "Can I export resumes as Word documents?" to "Yes, Pro users can download resumes as plain formatted .docx sheets optimized for ATS scanner rules, in addition to pixel-perfect visual styles.",
                    "How long is the 7-day money back guarantee?" to "If you upgrade and find the AI optimization tools or templates aren't for you, shoot our email support a quick line within 7 days for a 100% refund, no questions asked.",
                    "What is the STAR method for bullet points?" to "STAR stands for Situation, Task, Action, and Result. Recounting achievements with specific metric results (e.g. 'boosted speeds by 30% using Kotlin caching') represents the gold standard.",
                    "Are my resumes kept private and secure?" to "Yes, all data resides in encrypted secure Storage on your device and inside encrypted cloud tables. Share links are read-only and deactivated instantly on toggle.",
                    "Does the free tier expire?" to "No! The Free plan lets you draft and keep up to 2 high fidelity resumes forever, with limited AI optimization calls.",
                    "How does the referral scheme work?" to "Each Pro account has a unique referral code. Under your dashboard, copy this key. For every job seeker who inserts this code and upgrades, you get a full Month of free Pro subscription!"
                )

                faqs.forEachIndexed { index, (q, a) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable { activeFaqIndex = if (activeFaqIndex == index) -1 else index }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(q, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(0.9f))
                                Icon(
                                    imageVector = if (activeFaqIndex == index) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            AnimatedVisibility(visible = activeFaqIndex == index) {
                                Text(
                                    text = a,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- MOCK STRIPE CHECKOUT MODAL ---
    if (showStripeCheckoutPlan != null) {
        AlertDialog(
            onDismissRequest = { showStripeCheckoutPlan = null },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedPlan = showStripeCheckoutPlan!!
                        showStripeCheckoutPlan = null
                        onSimulatePremiumBuy(selectedPlan)
                    }
                ) {
                    Text("Pay to Secure License")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStripeCheckoutPlan = null }) {
                    Text("Cancel")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stripe Secure Checkout")
                }
            },
            text = {
                Column {
                    Text("You are purchasing ResumeAI Pro:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val planDesc = when (showStripeCheckoutPlan) {
                        "pro_yearly" -> "Pro Annual Access: $99 (billed yearly)"
                        "pro_monthly" -> "Pro Monthly Plan: $14.99 / month"
                        else -> "Lifetime Unlimited Plan: $79 (one-time pay)"
                    }
                    Text(planDesc, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Powered globally by Strype. This modal simulates standard Stripe Checkout checkout sessions including VAT in 40+ countries.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, tint = Color(0xFF0284C7), contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("7-Day Money-Back Guarantee Included", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    }
                }
            }
        )
    }
}

@Composable
fun PricingCard(
    title: String,
    price: String,
    period: String,
    features: List<String>,
    buttonText: String,
    highlight: Boolean,
    gradient: Boolean = false,
    onClick: () -> Unit
) {
    val bgModifier = if (gradient) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF1E1B4B),
                        Color(0xFF311042)
                    )
                )
            )
            .border(1.dp, Color(0xFFC084FC).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    } else if (highlight) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
    } else {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
    }

    val textColor = if (gradient) Color.White else MaterialTheme.colorScheme.onSurface
    val textMutedColor = if (gradient) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Card(
        modifier = bgModifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                if (highlight && !gradient) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("POPULAR", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (gradient) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFC084FC), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("LAUNCH DEAL", color = Color(0xFF311042), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, color = textColor, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text(period, color = textMutedColor, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp, start = 2.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (gradient) Color(0xFFC084FC) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, color = textColor, fontSize = 11.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (gradient) Color(0xFFC084FC) else if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    contentColor = if (gradient) Color(0xFF1E1B4B) else if (highlight) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Text(buttonText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun TestimonialWidget(review: Testimonial, modifier: Modifier) {
    Card(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(review.name.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(review.name, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("${review.role} · ${review.placed}", style = MaterialTheme.typography.bodySmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("\"${review.comment}\"", fontSize = 10.sp, lineHeight = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        }
    }
}

data class Testimonial(
    val name: String,
    val role: String,
    val placed: String,
    val comment: String
)

private fun getDemoBulletPoints(title: String): List<String> {
    return when {
        title.contains("Web", true) || title.contains("Front", true) -> listOf(
            "Architected decoupled React modules utilizing custom context hooks, speeding rendering latency by 24%.",
            "Refactored Vite asset pipelines using tree-shaking, reducing total bundle package dimensions from 1.8MB to 400KB."
        )
        title.contains("Android", true) || title.contains("Mobile", true) -> listOf(
            "Built modular Jetpack Compose dynamic frames implementing M3 components, increasing mobile session retention by 15%.",
            "Engineered local SQL Room repository networks caching responses securely, slashing network bandwidth overheads by 40%."
        )
        title.contains("Product", true) || title.contains("Manager", true) -> listOf(
            "Directed roadmaps for 3 cross-functional developer squads, launching MVP features 3 weeks ahead of scheduled timelines.",
            "Synthesized telemetry research statistics, defining product growth features that expanded trial conversion loops by 12%."
        )
        else -> listOf(
            "Designed and optimized system data workflows in high-volume environments, achieving a 30% reduction in query cycle speeds.",
            "Drafted rigorous clean documentation frameworks and integrated robust monitoring configurations for zero downtime deployments."
        )
    }
}
