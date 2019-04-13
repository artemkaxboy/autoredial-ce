package com.artemkaxboy.android.autoredialce.dsim;

import android.content.Intent;
import android.util.Log;

/**
 * Created by artem.kolin on 2016/04/06.
 * Заряжает intent для двухсимочных аппаратов
 */
public class DSimCommand {
    public static void powerIntent( Intent call, int sim_slot ) {
        if( sim_slot < 0 ) return;
        Log.w( "A##", "sim:" + sim_slot );
        // found in internet
        call.putExtra("com.android.phone.extra.sim", sim_slot);
        call.putExtra("com.android.phone.extra.slot", sim_slot);
        call.putExtra("com.android.phone.extra.simId", sim_slot);
        call.putExtra("com.android.phone.extra.simid", sim_slot);
        call.putExtra("com.android.phone.extra.simSlot", sim_slot);
        call.putExtra("com.android.phone.extra.simNumber", sim_slot);
        call.putExtra("com.android.phone.extra.id", sim_slot);
        call.putExtra("com.android.phone.extra.Card", sim_slot);
        call.putExtra("subscription", sim_slot);
        call.putExtra("phone_type", sim_slot);
        call.putExtra("simSlot", sim_slot);
        call.putExtra("SimSlot", sim_slot);
        call.putExtra("simslot", sim_slot);
        call.putExtra("slotId", sim_slot);
        call.putExtra("slotid", sim_slot);
        call.putExtra("simId", sim_slot);
        call.putExtra("simid", sim_slot);
        call.putExtra("subId", sim_slot);
        call.putExtra("Card", sim_slot);
        call.putExtra("Slot", sim_slot);
        call.putExtra("Sim", sim_slot);
        call.putExtra("id", sim_slot);
        // Added for ASUS Zenphone5
        call.putExtra("extra_asus_dial_use_dualsim", sim_slot);
        call.putExtra( "com.pekall.phone.extra.DSDS_CALL_FROM_SLOT_2",
                sim_slot > 0 );
    }
}
