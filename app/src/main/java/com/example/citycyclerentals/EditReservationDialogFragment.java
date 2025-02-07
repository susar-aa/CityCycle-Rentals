package com.example.citycyclerentals;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.citycyclerentals.models.ReservationAdmin;

public class EditReservationDialogFragment extends DialogFragment {

    private EditText etName, etStartDate, etEndDate, etTotalPrice, etContactNumber, etNIC, etDiscount;
    private Spinner spStatus, spPaymentMethod;
    private Button btnSave, btnCancel;
    private ReservationAdmin reservation;
    private OnSaveListener saveListener;

    public static EditReservationDialogFragment newInstance(ReservationAdmin reservation) {
        EditReservationDialogFragment fragment = new EditReservationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("reservation", reservation);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_reservation_dialog_fragment, container, false);
        etName = view.findViewById(R.id.etName);
        etContactNumber = view.findViewById(R.id.etContactNumber);
        etNIC = view.findViewById(R.id.etNIC);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        etTotalPrice = view.findViewById(R.id.etTotalPrice);
        etDiscount = view.findViewById(R.id.etDiscount);
        spStatus = view.findViewById(R.id.spStatus);
        spPaymentMethod = view.findViewById(R.id.spPaymentMethod);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        if (getArguments() != null && getArguments().containsKey("reservation")) {
            reservation = (ReservationAdmin) getArguments().getSerializable("reservation");
            if (reservation != null) {
                etName.setText(reservation.getName());
                etContactNumber.setText(reservation.getContactNumber());
                etNIC.setText(reservation.getNic());
                etStartDate.setText(reservation.getStartDate());
                etEndDate.setText(reservation.getEndDate());
                etTotalPrice.setText(String.valueOf(reservation.getTotalPrice()));
                etDiscount.setText(String.valueOf(reservation.getDiscount()));

                ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.status_array, android.R.layout.simple_spinner_item);
                statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spStatus.setAdapter(statusAdapter);
                spStatus.setSelection(statusAdapter.getPosition(reservation.getStatus()));

                ArrayAdapter<CharSequence> paymentMethodAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.payment_method_array, android.R.layout.simple_spinner_item);
                paymentMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPaymentMethod.setAdapter(paymentMethodAdapter);
                spPaymentMethod.setSelection(paymentMethodAdapter.getPosition(reservation.getPaymentMethod()));
            }
        }

        btnSave.setOnClickListener(v -> saveReservation());
        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    private void saveReservation() {
        String name = etName.getText().toString();
        String contactNumber = etContactNumber.getText().toString();
        String nic = etNIC.getText().toString();
        String startDate = etStartDate.getText().toString();
        String endDate = etEndDate.getText().toString();
        String totalPrice = etTotalPrice.getText().toString();
        String discount = etDiscount.getText().toString();
        String status = spStatus.getSelectedItem().toString();
        String paymentMethod = spPaymentMethod.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(contactNumber) || TextUtils.isEmpty(nic) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate) || TextUtils.isEmpty(totalPrice) || TextUtils.isEmpty(discount)) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        reservation.setName(name);
        reservation.setContactNumber(contactNumber);
        reservation.setNic(nic);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setTotalPrice(Double.parseDouble(totalPrice));
        reservation.setDiscount(Double.parseDouble(discount));
        reservation.setStatus(status);
        reservation.setPaymentMethod(paymentMethod);

        if (saveListener != null) {
            saveListener.onSave(reservation);
        }

        dismiss();
    }

    public void setOnSaveListener(OnSaveListener listener) {
        this.saveListener = listener;
    }

    public interface OnSaveListener {
        void onSave(ReservationAdmin reservation);
    }
}