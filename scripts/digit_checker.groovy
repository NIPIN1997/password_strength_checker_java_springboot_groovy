enabled = true
if (password =~ /[0-9]/) {
    passed = true
} else {
    passed = false
    message = "Password should contain at least one digit."
}