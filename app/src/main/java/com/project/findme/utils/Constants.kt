package com.project.findme.utils

import com.ryan.findme.R

object Constants {
    const val DEFAULT_PROFILE_PICTURE_URL =
        "https://firebasestorage.googleapis.com/v0/b/social-network-662a2.appspot.com/o/avatar.png?alt=media&token=69f56ce2-8fe2-4051-9e61-9e52182384c9"
    const val MAX_USERNAME_LENGTH = 8
    const val MIN_USERNAME_LENGTH = 3
    const val MIN_PASSWORD_LENGTH = 8
    const val SEARCH_TIME_DELAY = 500L
    const val FRAGMENT_ARG_KEY = "FRAGMENT_ARG_KEY"
    const val PAGE_SIZE = 5
    val FRAGMENTS_LIST = listOf(
        R.id.editProfileFragment,
        R.id.createPostFragment,
        R.id.changePasswordFragment,
        R.id.searchedProfileFragment,
        R.id.listFollowersFragment,
        R.id.listFollowersFragmentUser,
        R.id.commentFragment,
        R.id.likedByFragment
    )
    val hobbies = arrayOf(
        "3d printing",
        "amateur meteorology",
        "art collecting",
        "astronomy",
        "backpacking travel",
        "bartending",
        "base jumping",
        "beekeeping",
        "billiards",
        "bird watching",
        "blogging",
        "book collecting",
        "bowling",
        "build model rockets",
        "camping",
        "candle-making",
        "choreography",
        "climbing",
        "collecting autograph",
        "collecting cards",
        "collecting coins",
        "collecting comic books",
        "collecting dolls",
        "collecting insects",
        "collecting records",
        "collecting stamps",
        "collecting video games",
        "computer programming",
        "cooking specialty foods",
        "crocheting",
        "cubing",
        "dancing",
        "dowsing",
        "drone",
        "electronics",
        "equestrianism",
        "fantasy sports",
        "fashion",
        "fishing",
        "flower arranging",
        "flying",
        "foraging",
        "fossil hunting",
        "gardening",
        "geocaching",
        "ghost hunting",
        "graffiti art",
        "gymnastics",
        "herping",
        "high-power rocketry",
        "hiking",
        "hunting",
        "hydroponic gardening",
        "ice skating",
        "interior decorating",
        "jogging",
        "kart racing",
        "kayaking",
        "knitting",
        "martial arts",
        "meditation",
        "metal detecting",
        "microscopy",
        "mineral collecting",
        "model building",
        "mountain biking",
        "mountaineering",
        "movie collecting",
        "mushroom hunting",
        "observation hobby",
        "painting",
        "paper making",
        "parkour",
        "photography",
        "ping pong",
        "playing board games",
        "pottery",
        "quilting",
        "racing",
        "reading",
        "river rafting",
        "road biking",
        "robotics",
        "running",
        "sailing",
        "sand art",
        "scouting",
        "scuba diving",
        "sculpting",
        "sewing",
        "shopping",
        "skateboarding",
        "skiing",
        "soap-making",
        "social media",
        "social networking",
        "speedcubing",
        "surfing",
        "swimming",
        "taxidermy",
        "table tennis",
        "technology gadgets",
        "topiary",
        "treasure hunting",
        "urban exploration",
        "video gaming",
        "walking",
        "weightlifting",
        "whale watching",
        "wine collecting",
        "worldbuilding",
        "writing",
        "yoga"
    )

    val professions = arrayOf(
        "accountant",
        "actor",
        "actress",
        "air traffic controller",
        "architect",
        "artist",
        "attorney",
        "banker",
        "bartender",
        "barber",
        "bookkeeper",
        "builder",
        "businessman",
        "businesswoman",
        "businessperson",
        "butcher",
        "carpenter",
        "cashier",
        "chef",
        "coach",
        "dental hygienist",
        "dentist",
        "designer",
        "developer",
        "dietitian",
        "doctor",
        "economist",
        "editor",
        "electrician",
        "engineer",
        "farmer",
        "filmmaker",
        "fisherman",
        "flight attendant",
        "jeweler",
        "judge",
        "lawyer",
        "mechanic",
        "musician",
        "nutritionist",
        "nurse",
        "optician",
        "painter",
        "pharmacist",
        "photographer",
        "physician",
        "physician's assistant",
        "pilot",
        "plumber",
        "police officer",
        "politician",
        "professor",
        "programmer",
        "psychologist",
        "receptionist",
        "salesman",
        "salesperson",
        "saleswoman",
        "secretary",
        "singer",
        "student",
        "surgeon",
        "teacher",
        "therapist",
        "translator",
        "translator",
        "undertaker",
        "veterinarian",
        "videographer",
        "waiter",
        "waitress",
        "writer"
    )
}