package com.scribblefit.feature.workouts.domain

class InvalidWorkoutDateException : IllegalArgumentException("Workout date cannot be negative")