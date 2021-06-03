package com.project.cdh;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.project.cdh.ui.AddCholesterolAndFatsTest;
import com.project.cdh.ui.AddFBStest;
import com.project.cdh.ui.AddGlucoseTest;
import com.project.cdh.ui.AddHypertensionTest;
import com.project.cdh.ui.AddKidneysTest;
import com.project.cdh.ui.AddLiverTest;
import com.project.cdh.ui.AddPatientIdentifier;
import com.project.cdh.ui.ComprehensiveTest;
import com.project.cdh.ui.DViewIdentifiersP;
import com.project.cdh.ui.DoctorAccountActivity;
import com.project.cdh.ui.DoctorListActivity;
import com.project.cdh.ui.DoctorSendRequestActivity;
import com.project.cdh.ui.Functions;
import com.project.cdh.ui.HbAlc;
import com.project.cdh.ui.PatientAccountActivity;
import com.project.cdh.ui.DoctorPatientMedicalHistory;
import com.project.cdh.ui.PatientReceiveRequestActivity;
import com.project.cdh.ui.ViewIdentifiersP;
import com.project.cdh.ui.ViewMedicines;
import com.project.cdh.ui.WelcomeActivity;
import com.project.cdh.ui.addComplications;
import com.project.cdh.ui.addNewTestAppointment;
import com.project.cdh.ui.newChronicDiseases;
import com.project.cdh.ui.updateComplicationStatus;
import com.project.cdh.ui.viewComplications;
import com.project.cdh.ui.viewComplicationsP;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class DrawerUtil
{
    public static Drawer drawer;
    public static View headerView;
    public static Toolbar toolbar;

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

                        new PrimaryDrawerItem().withIdentifier(2)
                                .withName("Add Patients").withIcon(R.drawable.ic_add_patient)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(3)
                                .withName("Patients Medical History").withIcon(R.drawable.ic_medical_history)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(4)
                                .withName("Patients Charts").withIcon(R.drawable.ic_barchart)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(18)
                                .withName("View Identifier").withIcon(R.drawable.ic_relatives)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(5)
                                .withName("New Appointment").withIcon(R.drawable.appointmenicon)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(6)
                                .withName("Manage Patients Complications").withIcon(R.drawable.compier)
                                .withIconTintingEnabled(true)
                                .withSubItems(
                                        new SecondaryDrawerItem().withIdentifier(7)
                                                .withName("New Complication").withIcon(R.drawable.complication)
                                                .withIconTintingEnabled(true),
                                        new SecondaryDrawerItem().withIdentifier(8)
                                                .withName("UPDATE Complication").withIcon(R.drawable.updatecomp)
                                                .withIconTintingEnabled(true),
                                        new SecondaryDrawerItem().withIdentifier(19)
                                                .withName("View Complications").withIcon(R.drawable.ic_complications)
                                                .withIconTintingEnabled(true)
                                ),

                        new PrimaryDrawerItem().withIdentifier(20)
                                .withName("Patients Log Medical").withIcon(R.drawable.medical_history)
                                .withIconTintingEnabled(true),

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withIdentifier(14)
                                .withName("LogOut").withIcon(R.drawable.ic_logout)
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
                            {
                                Intent intent = new Intent(activity,  DoctorAccountActivity.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

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
                               // Intent intent = new Intent(activity, patientMedicalRecords.class);
                               // view.getContext().startActivity(intent);
                               // break;
                            }

                            case 4:
                                break;

                            case 5:
                            {
                                Intent intent = new Intent(activity, addNewTestAppointment.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 6:
                                break;

                            case 7:
                            {
                                Intent intent = new Intent(activity, addComplications.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 8:
                            {
                                Intent intent = new Intent(activity, updateComplicationStatus.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 18:
                            {
                                Intent intent = new Intent(activity, DViewIdentifiersP.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 19:
                            {
                                Intent intent = new Intent(activity, viewComplicationsP.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 20:
                            {
                                Intent intent = new Intent(activity, DoctorPatientMedicalHistory.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 14:
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

                        new PrimaryDrawerItem().withIdentifier(2)
                                .withName("New Requests").withIcon(R.drawable.ic_new_requests)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(3)
                                .withName("My Doctors").withIcon(R.drawable.ic_my_doctors)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(15)
                                .withName("Add Chronic Diseases").withIcon(R.drawable.ic_add_chronic_disease)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(19)
                                .withName("View Complications").withIcon(R.drawable.ic_complications)
                                .withIconTintingEnabled(true),

                        new PrimaryDrawerItem().withIdentifier(16)
                                .withName("Manage Relatives").withIcon(R.drawable.ic_relatives)
                                .withIconTintingEnabled(true)
                                .withSubItems(
                                        new SecondaryDrawerItem().withIdentifier(17)
                                                .withName("New Relative"),
                                        new SecondaryDrawerItem().withIdentifier(18)
                                                .withName("View Relatives")
                                ),

                      /*  new PrimaryDrawerItem().withIdentifier(13)
                                .withName("Medical Records").withIcon(R.drawable.medical_history)
                                .withIconTintingEnabled(true),*/

                       /* new PrimaryDrawerItem().withIdentifier(20)
                                .withName("Log Medical").withIcon(R.drawable.medical_history)
                                .withIconTintingEnabled(true),*/

                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withIdentifier(4)
                                .withName("New Test").withIcon(R.drawable.ic_new_test)
                                .withIconTintingEnabled(true)
                                .withSubItems(
                                        new SecondaryDrawerItem().withIdentifier(5)
                                                .withName("Glucose Test"),

                                        new SecondaryDrawerItem().withIdentifier(6)
                                                .withName("F.B.S Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(7)
                                                .withName("Hypertension Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(8)
                                                .withName("Cumulative Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(9)
                                                .withName("Kidneys Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(10)
                                                .withName("LiverTest")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(11)
                                                .withName("Cholesterol And Fats Test")
                                        ,
                                        new SecondaryDrawerItem().withIdentifier(12)
                                                .withName("Comprehensive Test")
                                ),


                        new PrimaryDrawerItem().withIdentifier(21)
                                .withName("Medicines").withIcon(R.drawable.ic_medicines)
                                .withIconTintingEnabled(true),


                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withIdentifier(14)
                                .withName("LogOut").withIcon(R.drawable.ic_logout)
                                .withIconTintingEnabled(true)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {

                        if((int) drawerItem.getIdentifier() != 4)
                            drawer.closeDrawer();

                       
                        switch ((int) drawerItem.getIdentifier())
                        {
                            case 0:
                            {
                                Intent intent = new Intent(activity, PatientAccountActivity.class);
                                view.getContext().startActivity(intent);
                                break;
                            }
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
                                break;

                            case 5:
                            {
                                Intent I = new Intent(activity, AddGlucoseTest.class);
                                view.getContext().startActivity(I);
                                break;
                            }

                            case 6:
                            {
                                if(Functions.pact == 6)break;
                                Intent intent = new Intent(activity, AddFBStest.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 7:
                            {
                                Intent intent = new Intent(activity, AddHypertensionTest.class);
                                view.getContext().startActivity(intent);
                                break;
                            }


                            case 8:
                            {
                                if(Functions.pact == 8)break;
                                Intent intent = new Intent(activity, HbAlc.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 9:
                            {
                                Intent intent = new Intent(activity, AddKidneysTest.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 10:
                            {
                                Intent intent = new Intent(activity,  AddLiverTest.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 11:
                            {
                                Intent intent = new Intent(activity,  AddCholesterolAndFatsTest.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 12:
                            {
                                Intent intent = new Intent(activity, ComprehensiveTest.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 15:
                            {
                                Intent intent = new Intent(activity, newChronicDiseases.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            /*case 13:
                            {
                                Intent intent = new Intent(activity, medicalRecords.class);
                                view.getContext().startActivity(intent);
                                break;
                            }*/

                           /* case 20:
                            {
                                Intent intent = new Intent(activity, PatientMedicalHistoryActivity.class);
                                view.getContext().startActivity(intent);
                                break;
                            }*/

                            case 16:
                                break;

                            case 17:
                            {
                                Intent intent = new Intent(activity, AddPatientIdentifier.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 18:
                            {
                                Intent intent = new Intent(activity, ViewIdentifiersP.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 19:
                            {
                                Intent intent = new Intent(activity, viewComplications.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 21:
                            {
                                Intent intent = new Intent(activity, ViewMedicines.class);
                                view.getContext().startActivity(intent);
                                break;
                            }

                            case 14:
                            {
                                AlertDialog.Builder   x= new AlertDialog.Builder ( view.getContext() );
                                x.setMessage ( "Do You Want to Logout?" ).setTitle ( "Patient LogOut" )

                                        .setPositiveButton ( "YES_EXIT", new DialogInterface.OnClickListener () {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
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


   public static void clearSelection()
    {
        drawer.setSelection(-1);
    }

}
