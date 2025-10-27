// RunDiary.java
// Created by juliette richards 9/10/2025
// The purpose of this program is to give the user a suggested mileage for the next week based on logged runs for the current week
// Users can either add runs or view a snapshot for the current week
// Weeks are based off monday - friday calander week

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
// Do not delete ^

import java.text.DecimalFormat;

public class RunDiary {

// Monday to Sunday weeks, week key = the Monday date of that week
private static final String DATE_PATTERN = "MM/dd/yyyy";
private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
private static final DayOfWeek WEEK_START = DayOfWeek.MONDAY;

// Map of week-key (Monday date) to weekly totals
private final Map<LocalDate, WeekTotals> weeks = new HashMap<>();

public static void main(String[] args) {
new RunDiary().run();
}
// This is the main loop that shows the menu and handles user choices
private void run() {
Scanner in = new Scanner(System.in);
System.out.println("Welcome to RunDiary");
System.out.println("Weeks are Monday → Sunday. Safe next-week suggestion uses the 10% rule.\n");

while (true) {
System.out.println("=== Menu ===");
System.out.println("1) Add Run");
System.out.println("2) View Current Week snapshot");
System.out.print("Choose an option: ");

String raw = in.nextLine().trim();
int choice;
try {
choice = Integer.parseInt(raw);
} catch (NumberFormatException ex) {
System.out.println("Invalid option (not a number).\n");
continue;
}

if (choice == 1) {
addRun(in);
} else if (choice == 2) {
viewCurrentWeek();
} else {
System.out.println("Invalid option.\n");
}
}
}

// Option 1: Add Run (date + miles as a decimal number) 
private void addRun(Scanner in) {
System.out.println("\n-- Add Run --");

LocalDate date = readDate(in, "Date (MM/dd/yyyy): ");
if (date == null) {
System.out.println("Invalid date format. Please use MM/dd/yyyy.\n");
return;
}

Double miles = readDouble(in, "Miles (numeric): ");
if (miles == null || miles <= 0) {
System.out.println("Invalid input for miles. Please enter a positive numeric value.\n");
return;
}

LocalDate weekKey = toWeekKey(date); // Monday of that week

WeekTotals wt = weeks.get(weekKey);
if (wt == null) {
wt = new WeekTotals(weekKey);
weeks.put(weekKey, wt);
}
wt.addRun(miles);

double suggestedNext = roundToTenth(wt.totalMiles * 1.10);

System.out.println("Run added to week of " + formatDate(weekKey) + ".");
System.out.printf(Locale.US, "Weekly total: %.1f miles (%d run%s).\n",
wt.totalMiles, wt.totalRuns, wt.totalRuns == 1 ? "" : "s");
System.out.printf(Locale.US, "Suggested next week mileage: %.1f miles.\n\n", suggestedNext);
}

// Option 2: View Current Week Totals & Suggested Mileage 
private void viewCurrentWeek() {
System.out.println("\n-- Current Week Totals & Suggested Mileage --");
LocalDate today = LocalDate.now();
LocalDate weekKey = toWeekKey(today);

WeekTotals wt = weeks.get(weekKey);
if (wt == null) {
System.out.println("No runs logged for the week of " + formatDate(weekKey) + ".");
System.out.println("Suggested next week mileage is unavailable until you add at least one run.\n");
return;
}

double suggestedNext = roundToTenth(wt.totalMiles * 1.10);

System.out.println("Week of " + formatDate(weekKey) + ":");
System.out.printf(Locale.US, "Weekly total: %.1f miles (%d run%s).\n",
wt.totalMiles, wt.totalRuns, wt.totalRuns == 1 ? "" : "s");
System.out.printf(Locale.US, "Suggested next week mileage: %.1f miles.\n\n", suggestedNext);
}


// Convert any date to the Monday of its Monday to Sunday week
private LocalDate toWeekKey(LocalDate date) {
int shift = (date.getDayOfWeek().getValue() - WEEK_START.getValue() + 7) % 7;
return date.minusDays(shift);
}

private static String formatDate(LocalDate d) {
return d.format(DATE_FMT);
}

private static Double readDouble(Scanner in, String prompt) {
System.out.print(prompt);
String s = in.nextLine().trim();
try {
return Double.parseDouble(s);
} catch (NumberFormatException ex) {
return null;
}
}

private static LocalDate readDate(Scanner in, String prompt) {
System.out.print(prompt);
String s = in.nextLine().trim();
try {
return LocalDate.parse(s, DATE_FMT);
} catch (Exception ex) {
return null;
}
}
// This is the math so that the program can calculate the 10% suggested miles
private static double roundToTenth(double x) {
return Math.round(x * 10.0) / 10.0;
}

// Simple totals holder for a week
private static class WeekTotals {
final LocalDate weekKey; // Monday date
int totalRuns = 0;
double totalMiles = 0.0;

WeekTotals(LocalDate weekKey) {
this.weekKey = weekKey;
}
void addRun(double miles) {
totalRuns += 1;
totalMiles += miles;
}
}
}
