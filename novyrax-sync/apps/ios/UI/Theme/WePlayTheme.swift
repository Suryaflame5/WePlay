import SwiftUI

public struct WePlayColors {
    // Light Canvas & Surface
    public static let canvasLight = Color(red: 247/255, green: 246/255, blue: 240/255) // #F7F6F0
    public static let surfaceLight = Color.white
    public static let surfaceSubtleLight = Color(red: 240/255, green: 239/255, blue: 233/255)
    public static let textPrimaryLight = Color(red: 23/255, green: 27/255, blue: 29/255) // #171B1D
    public static let textSecondaryLight = Color(red: 133/255, green: 139/255, blue: 137/255) // #858B89
    public static let separatorLight = Color(red: 228/255, green: 227/255, blue: 220/255) // #E4E3DC

    // Dark Canvas & Surface
    public static let canvasDark = Color(red: 18/255, green: 20/255, blue: 21/255) // #121415
    public static let surfaceDark = Color(red: 26/255, green: 29/255, blue: 31/255) // #1A1D1F
    public static let surfaceSubtleDark = Color(red: 34/255, green: 38/255, blue: 41/255)
    public static let textPrimaryDark = Color(red: 236/255, green: 238/255, blue: 234/255) // #ECEEEA
    public static let textSecondaryDark = Color(red: 133/255, green: 139/255, blue: 137/255)
    public static let separatorDark = Color(red: 40/255, green: 44/255, blue: 48/255) // #282C30

    // Universal Accents & Functional States
    public static let accentTeal = Color(red: 33/255, green: 205/255, blue: 184/255) // #21CDB8
    public static let accentTealSubtle = Color(red: 33/255, green: 205/255, blue: 184/255, opacity: 0.12)
    public static let stateWarning = Color(red: 230/255, green: 126/255, blue: 34/255) // #E67E22
    public static let stateError = Color(red: 217/255, green: 83/255, blue: 79/255) // #D9534F
    public static let stateSuccess = Color(red: 46/255, green: 204/255, blue: 113/255) // #2ECC71
}

public struct WePlaySpacing {
    public static let xs: CGFloat = 4
    public static let s: CGFloat = 8
    public static let m: CGFloat = 12
    public static let l: CGFloat = 16
    public static let xl: CGFloat = 20
    public static let xxl: CGFloat = 24
    public static let xxxl: CGFloat = 32
}
