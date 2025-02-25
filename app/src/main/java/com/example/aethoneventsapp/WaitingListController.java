package com.example.aethoneventsapp;

/**
 * The WaitingListController class manages operations for handling waitlist entries,
 * including joining, leaving, selecting, and tracking entrants on the waitlist.
 */
public class WaitingListController {

    private final WaitingList waitingList;


    /**
     * Constructor for WaitingListController with maximum waitlist size.
     * Initializes a new WaitingList instance for managing waitlist operations.
     */
    public WaitingListController(String waitlistId, String eventId, int maxWaitlistSize) {
        // Unique identifier for the waitlist entry
        // Identifier of the event associated with this waitlist
        this.waitingList = new WaitingList(waitlistId, eventId, maxWaitlistSize);
    }

    /**
     * Constructor for WaitingListController with no maximum waitlist size.
     * Initializes a new WaitingList instance for managing waitlist operations.
     */
    public WaitingListController(String waitlistId, String eventId) {
        // Unique identifier for the waitlist entry
        // Identifier of the event associated with this waitlist
        this.waitingList = new WaitingList(waitlistId, eventId);
    }


    /**
     * Adds an entrant to the waitlist for a given event.
     *
     * @param eventId   the ID of the event to join the waitlist for
     * @param entrantId the ID of the entrant joining the waitlist
     */
    public void joinWaitlist(String eventId, String entrantId) {
        waitingList.addEntrantToWaitlist(eventId, entrantId);
    }

    /**
     * Removes an entrant from the waitlist based on the entrant ID.
     *
     * @param entrantId the ID of the entrant to be removed from the waitlist
     */
    public void unjoinWaitlist(String entrantId) {
        waitingList.removeEntrantFromWaitlist(entrantId);
    }

    /**
     * Selects entrants from the waitlist for an event, processing them for selection.
     *
     * @param eventId the ID of the event for which entrants are to be selected
     */
    public void selectEntrantsForEvent(String eventId, int vacancies) {
        waitingList.manageEntrantSelection(eventId, vacancies);
    }
}

