package com.example.healthza;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.healthza.ui.DoctorListActivity;
import com.example.healthza.ui.DoctorSendRequestActivity;
import com.example.healthza.ui.PatientReceiveRequestActivity;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class DrawerUtil
{
    public static View headerView;

    public static void getDoctorDrawer(final Activity activity, int identifier)
    {
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(identifier)
                .withHeader(headerView)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(0)
                                .withName("Account").withIcon(R.drawable.ic_account)
                                .withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(1)
                                .withName("Notification").withIcon(R.drawable.ic_notifications)
                                .withIconTintingEnabled(true),
                        new DividerDrawerItem(),
                                new SecondaryDrawerItem().withIdentifier(2)
                                        .withName("Add Patients").withIcon(R.drawable.ic_add_patient)
                                        .withIconTintingEnabled(true),
                                new SecondaryDrawerItem().withIdentifier(3)
                                        .withName("Patients Medical History").withIcon(R.drawable.ic_medical_history)
                                        .withIconTintingEnabled(true),
                        new SecondaryDrawerItem().withIdentifier(4)
                                .withName("Patients Charts").withIcon(R.drawable.ic_barchart)
                                .withIconTintingEnabled(true)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        switch ((int) drawerItem.getIdentifier())
                        {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                            {
                                Intent intent = new Intent(activity, DoctorSendRequestActivity.class);
                                view.getContext().startActivity(intent);
                                break;
                            }
                            case 3:
                            {

                            }
                            case 4:
                            {
                                //TODO
                                break;
                            }
                        }
                        return true;
                    }
                })
                .build();
    }


    public static void getPatientDrawer(final Activity activity, int identifier)
    {
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withSliderBackgroundColorRes(R.color.white)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(identifier)
                .withHeader(headerView)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(0)
                                .withName("Account").withIcon(R.drawable.ic_account)
                                .withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(1)
                                .withName("Notification").withIcon(R.drawable.ic_notifications)
                                .withIconTintingEnabled(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(2)
                                .withName("New Requests").withIcon(R.drawable.ic_notifications)
                                .withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(3)
                                .withName("My Doctors").withIcon(R.drawable.ic_doctor)
                                .withIconTintingEnabled(true)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        switch ((int) drawerItem.getIdentifier())
                        {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                            {
                                Intent intent = new Intent(activity, PatientReceiveRequestActivity.class);
                                view.getContext().startActivity(intent);
                                break;
                            }
                            case 3:
                            {
                                Intent intent = new Intent(activity, DoctorListActivity.class);
                                view.getContext().startActivity(intent);
                                break;
                            }
                            case 4:

                            {
                                //TODO
                                break;
                            }
                        }

                        return true;
                    }
                })
                .build();
    }

}
