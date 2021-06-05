package com.project.cdh;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.project.cdh.ui.PatientAddCholesterolAndFatsTestActivity;
import com.project.cdh.ui.PatientAddChronicDiseaseActivity;
import com.project.cdh.ui.PatientAddFBSTestActivity;
import com.project.cdh.ui.PatientAddGlucoseTestActivity;
import com.project.cdh.ui.PatientAddHbAlcTestActivity;
import com.project.cdh.ui.PatientAddHypertensionTestActivity;
import com.project.cdh.ui.PatientAddKidneysTestActivity;
import com.project.cdh.ui.PatientAddLiverTestActivity;
import com.project.cdh.ui.PatientAddRelativeActivity;
import com.project.cdh.ui.PatientAddComprehensiveTestActivity;
import com.project.cdh.ui.DoctorViewPatientsRelativesActivity;
import com.project.cdh.ui.DoctorAccountActivity;
import com.project.cdh.ui.DoctorListActivity;
import com.project.cdh.ui.DoctorPatientMedicalHistoryActivity;
import com.project.cdh.ui.DoctorSendRequestActivity;
import com.project.cdh.ui.Functions;
import com.project.cdh.ui.PatientAccountActivity;
import com.project.cdh.ui.PatientReceiveRequestActivity;
import com.project.cdh.ui.PatientViewRelatives;
import com.project.cdh.ui.PatientMedicinesActivity;
import com.project.cdh.ui.WelcomeActivity;
import com.project.cdh.ui.DoctorAddPatientComplicationsActivity;
import com.project.cdh.ui.DoctorAddPatientAppointmentActivity;
import com.project.cdh.ui.DoctorUpdatePatientComplicationsActivity;
import com.project.cdh.ui.PatientViewComplicationsActivity;
import com.project.cdh.ui.DoctorViewPatientComplicationsActivity;

public class DrawerUtil
{
    public static Drawer drawer;
    public static View headerView;

    public static void getDoctorDrawer(final Activity activity, int identifier)
    {
        drawer = new DrawerBuilder()
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

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withIdentifier(1)
                                .withName("Add Patients").withIcon(R.drawable.ic_add_patient)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(2)
                                .withName("Patients Medical History").withIcon(R.drawable.ic_medical_history)
                                .withIconTintingEnabled(true),


                        new PrimaryDrawerItem().withIdentifier(3)
                                .withName("View Relatives").withIcon(R.drawable.ic_relatives)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(4)
                                .withName("New Appointment").withIcon(R.drawable.ic_appointments)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(5)
                                .withName("Manage Patients Complications").withIcon(R.drawable.compier)
                                .withIconTintingEnabled(true)
                                .withSubItems(
                                        new SecondaryDrawerItem().withIdentifier(6)
                                                .withName("New").withIcon(R.drawable.complication)
                                                .withIconTintingEnabled(true),
                                        new SecondaryDrawerItem().withIdentifier(7)
                                                .withName("UPDATE").withIcon(R.drawable.updatecomp)
                                                .withIconTintingEnabled(true),
                                        new SecondaryDrawerItem().withIdentifier(8)
                                                .withName("View").withIcon(R.drawable.ic_complications)
                                                .withIconTintingEnabled(true)
                                ),

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withIdentifier(9)
                                .withName("LogOut").withIcon(R.drawable.ic_logout)
                                .withIconTintingEnabled(true)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        if((int) drawerItem.getIdentifier() != 5)
                            drawer.closeDrawer();

                        switch ((int) drawerItem.getIdentifier())
                        {
                            case 0:
                            {
                                if(!(activity instanceof DoctorAccountActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorAccountActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 1:
                            {
                                if(!(activity instanceof DoctorSendRequestActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorSendRequestActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 2:
                            {
                                if(!(activity instanceof DoctorPatientMedicalHistoryActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorPatientMedicalHistoryActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }


                            case 3:
                            {
                                if(!(activity instanceof DoctorViewPatientsRelativesActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorViewPatientsRelativesActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 4:
                            {
                                if(!(activity instanceof DoctorAddPatientAppointmentActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorAddPatientAppointmentActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 5:
                                break;

                            case 6:
                            {
                                if(!(activity instanceof DoctorAddPatientComplicationsActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorAddPatientComplicationsActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 7:
                            {
                                if(!(activity instanceof DoctorUpdatePatientComplicationsActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorUpdatePatientComplicationsActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }


                            case 8:
                            {
                                if(!(activity instanceof DoctorViewPatientComplicationsActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorViewPatientComplicationsActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 9:
                            {
                                AlertDialog.Builder   x= new AlertDialog.Builder ( view.getContext() );
                                x.setMessage ( "DO YOU WANT TO LogOut?" ).setTitle ( "Patient LogOut" )

                                        .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseAuth.getInstance().signOut();
                                                activity.finishAffinity();
                                                Intent I = new Intent(activity, WelcomeActivity.class);
                                                view.getContext().startActivity(I);

                                            }
                                        } )

                                        .setNegativeButton ( "CANCEL", new DialogInterface.OnClickListener () {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) { }
                                        })

                                        .setIcon(R.drawable.qus)
                                        .setPositiveButtonIcon (view.getContext().getDrawable ( R.drawable.yes))
                                        .setNegativeButtonIcon(view.getContext().getDrawable ( R.drawable.no))
                                        .show ();

                                break;
                            }
                        }
                        return true;
                    }
                })
                .build();

        drawer.getDrawerLayout().setStatusBarBackgroundColor(activity.getColor(R.color.primary_dark));
    }


    public static void getPatientDrawer(final Activity activity, int identifier)
    {

        drawer = new DrawerBuilder()
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

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withIdentifier(1)
                                .withName("New Requests").withIcon(R.drawable.ic_new_requests)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(2)
                                .withName("My Doctors").withIcon(R.drawable.ic_my_doctors)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(3)
                                .withName("Add Chronic Diseases").withIcon(R.drawable.ic_add_chronic_disease)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(4)
                                .withName("View Complications").withIcon(R.drawable.ic_complications)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(5)
                                .withName("Manage Relatives").withIcon(R.drawable.ic_relatives)
                                .withIconTintingEnabled(true)
                                .withSubItems(
                                        new SecondaryDrawerItem().withIdentifier(6)
                                                .withName("New Relative"),
                                        new SecondaryDrawerItem().withIdentifier(7)
                                                .withName("View Relatives")
                                ),

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withIdentifier(8)
                                .withName("New Test").withIcon(R.drawable.ic_new_test)
                                .withIconTintingEnabled(true)
                                .withSubItems(
                                        new SecondaryDrawerItem().withIdentifier(9)
                                                .withName("Glucose Test"),

                                        new SecondaryDrawerItem().withIdentifier(10)
                                                .withName("F.B.S Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(11)
                                                .withName("Hypertension Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(12)
                                                .withName("Cumulative Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(13)
                                                .withName("Kidneys Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(14)
                                                .withName("LiverTest")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(15)
                                                .withName("Cholesterol And Fats Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(16)
                                                .withName("Comprehensive Test").withSelectable(true)
                                ),


                        new PrimaryDrawerItem().withIdentifier(17)
                                .withName("Medicines").withIcon(R.drawable.ic_medicines)
                                .withIconTintingEnabled(true),


                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withIdentifier(18)
                                .withName("LogOut").withIcon(R.drawable.ic_logout)
                                .withIconTintingEnabled(true)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {

                        if( (int) drawerItem.getIdentifier() != 8 &&
                                (int) drawerItem.getIdentifier() != 5)
                            drawer.closeDrawer();

                        switch ((int) drawerItem.getIdentifier())
                        {
                            case 0:
                            {
                                if(!(activity instanceof PatientAccountActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAccountActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 1:
                            {
                                if(!(activity instanceof PatientReceiveRequestActivity))
                                {
                                    Intent intent = new Intent(activity, PatientReceiveRequestActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }
                            case 2:
                            {
                                if(!(activity instanceof DoctorListActivity))
                                {
                                    Intent intent = new Intent(activity, DoctorListActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 3:
                            {
                                if(!(activity instanceof PatientAddChronicDiseaseActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddChronicDiseaseActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 4:
                            {
                                if(!(activity instanceof PatientViewComplicationsActivity))
                                {
                                    Intent intent = new Intent(activity, PatientViewComplicationsActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 5: break;

                            case 6:
                            {
                                if(!(activity instanceof PatientAddRelativeActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddRelativeActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                    break;
                            }

                            case 7:
                            {
                                if(!(activity instanceof PatientViewRelatives))
                                {
                                    Intent intent = new Intent(activity, PatientViewRelatives.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 8: break;

                            case 9:
                            {
                                if(!(activity instanceof PatientAddGlucoseTestActivity))
                                {
                                    Intent I = new Intent(activity, PatientAddGlucoseTestActivity.class);
                                    view.getContext().startActivity(I);
                                }
                                break;
                            }

                            case 10:
                            {
                                if(Functions.pact == 6)
                                    break;

                                if(!(activity instanceof PatientAddFBSTestActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddFBSTestActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 11:
                            {
                                if(!(activity instanceof PatientAddHypertensionTestActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddHypertensionTestActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }


                            case 12:
                            {
                                if(Functions.pact == 8)
                                    break;

                                if(!(activity instanceof PatientAddHbAlcTestActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddHbAlcTestActivity.class);
                                    view.getContext().startActivity(intent);
                                }

                                break;
                            }

                            case 13:
                            {
                                if(!(activity instanceof PatientAddKidneysTestActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddKidneysTestActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 14:
                            {
                                if(!(activity instanceof PatientAddLiverTestActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddLiverTestActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 15:
                            {
                                if(!(activity instanceof PatientAddCholesterolAndFatsTestActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddCholesterolAndFatsTestActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 16:
                            {
                                if(!(activity instanceof PatientAddComprehensiveTestActivity))
                                {
                                    Intent intent = new Intent(activity, PatientAddComprehensiveTestActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 17:
                            {
                                if(!(activity instanceof PatientMedicinesActivity))
                                {
                                    Intent intent = new Intent(activity, PatientMedicinesActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            }

                            case 18:
                            {
                                AlertDialog.Builder   x= new AlertDialog.Builder ( view.getContext() );
                                x.setMessage ( "Do You Want to Logout?" ).setTitle ( "Patient LogOut" )

                                        .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseAuth.getInstance().signOut();
                                                activity.finishAffinity();
                                                Intent I = new Intent(activity, WelcomeActivity.class);
                                                view.getContext().startActivity(I);

                                            }
                                        } )

                                        .setNegativeButton ( "CANCEL", new DialogInterface.OnClickListener () {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) { }
                                        })

                                        .setIcon(R.drawable.qus)
                                        .setPositiveButtonIcon (view.getContext().getDrawable ( R.drawable.yes))
                                        .setNegativeButtonIcon(view.getContext().getDrawable ( R.drawable.no))
                                        .show ();

                                break;
                            }

                        }

                        return true;
                    }
                })
                .build();

        drawer.getDrawerLayout().setStatusBarBackgroundColor(activity.getColor(R.color.primary_dark));
    }
}
