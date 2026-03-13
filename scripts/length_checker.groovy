enabled = true
String password=password
if (password.length() >= 8) {
    passed = true
} else {
    passed = false
    message = "Password should contain a minimum of 8 characters."
}
