# ♿ Exercise 10: Accessibility

**Branch Name:** `lesson-10-accessibility`
**Module:** Accessibility & Inclusion
**Estimated Time:** 35 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
In this exercise, you will make the app usable for users with vision impairments. You will reduce "swipe fatigue" by grouping elements and provide clear context for interactive states.

### **✅ Task 1: The "Chatty" Card**
By default, a screen reader makes you swipe once for the icon, once for the title, and once for the progress bar. We want the entire card to be one single "thought."
1.  **Open** `ui/components/DeckCard.kt`.
2.  **Action**: Add `Modifier.semantics(mergeDescendants = true)` to the top-level `Card`.
3.  **Refine**: Set a `contentDescription` that summarizes the deck (e.g., "${deck.title}, ${deck.cards.size} cards").

### **✅ Task 2: Custom Click Labels**
"Double tap to activate" doesn't tell a user what happens next.
1.  **Action**: In your semantics block, add `onClick(label = "Open Deck", action = null)`.
2.  **Result**: TalkBack will now say: "Physics 101. Double tap to **Open Deck**."

### **✅ Task 3: Flashcard Context**
In Study Mode, users need to know if they are looking at the Question side or the Answer side of a card.
1.  **Open** `ui/StudyScreen.kt`.
2.  **Action**: Add `stateDescription = if (isFlipped) "Answer" else "Question"` to the card's semantics.

### **🚀 Definition of Done**
* [ ] TalkBack selects the whole `DeckCard` in one swipe.
* [ ] The app announces "Open Deck" when a card is selected.
* [ ] When flipping a flashcard, the app announces its state (Question/Answer).
