package com.scribblefit.feature.sets.domain

class SetRepsNotValidException : Exception("Reps must be greater than 0")

class SetWeightNotValidException : Exception("Weight cannot be negative")

class SetNumberNotValidException : Exception("Set number must be greater than 0")
