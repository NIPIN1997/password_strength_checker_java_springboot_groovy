enabled = true
if (password =~ /[a-z]/) {
    passed = true
} else {
    passed = false
    message = "Password should contain at least one lowercase character."
}