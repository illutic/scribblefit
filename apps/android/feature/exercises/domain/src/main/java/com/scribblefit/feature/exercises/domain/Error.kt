package com.scribblefit.feature.exercises.domain

class ExerciseRepsNotValidException : Exception("Reps must be greater than 0")

class ExerciseSetsNotValidException : Exception("Sets must be greater than 0")

class ExerciseWeightNotValidException : Exception("Weight cannot be negative")

class ExerciseNameNotValidException : Exception("Exercise name cannot be blank")
