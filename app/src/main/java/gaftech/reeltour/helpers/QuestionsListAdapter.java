package gaftech.reeltour.helpers;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import gaftech.reeltour.R;
import gaftech.reeltour.models.Question;
import gaftech.reeltour.models.QuestionsOption;

/**
 * Created by r.suleymanov on 01.07.2015.
 * email: ruslancer@gmail.com
 */

public class QuestionsListAdapter extends ArrayAdapter<Question> {
    public class Answer {
        public Integer index;
        public Long question_id;
        public Long picked_option_id;
        public String comment;
    }

    private HashMap<Integer, Answer> checked = new HashMap<Integer, Answer>();

    public HashMap<Integer, Answer> getChecked() {
        return this.checked;
    }

    public QuestionsListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    public QuestionsListAdapter(Context context, int resource, List<Question> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.questions_item, null);
        }
        final Question p = getItem(position);
        if (p != null) {
            TextView questionText = (TextView) v.findViewById(R.id.questionText);
            if (questionText != null) {
                questionText.setText(p.getMessage());
            }
                //create radio group
                RadioGroup rg = (RadioGroup) v.findViewById(R.id.optionsContainer);
                rg.setTag(R.id.list_position, position);
                rg.setTag(R.id.question_id, p.getId());
                rg.removeAllViews();
                //if there are options
                if (!p.getShowOptions()) {
                    EditText te = new EditText(getContext());
                    te.setBackgroundResource(R.drawable.blue_bottom_border);
                    te.setTextColor(R.color.black);
                    te.setTag(R.id.radio_group, rg);
                    te.addTextChangedListener(new GenericTextWatcher(te) {
                        @Override
                        public void afterTextChanged(Editable s) {
                            RadioGroup gr = (RadioGroup) this.view.getTag(R.id.radio_group);
                            int position = (Integer) gr.getTag(R.id.list_position);
                            Answer a = (checked.containsKey(position)) ? checked.get(position) : (new Answer());
                            a.picked_option_id = (long) 0;
                            a.index = 0;
                            a.question_id = (Long)gr.getTag(R.id.question_id);
                            a.comment = s.toString();
                            checked.put(position, a);
                        }
                    });
                    Answer a = (Answer) checked.get(position);
                    if  (a != null) te.setText(a.comment);
                    rg.addView(te, 0, new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                } else {
                    int j = 0;
                    for (int i = 0; i < p.options.size(); i++) {
                        QuestionsOption qo = p.options.get(i);
                        //create radio button
                        RadioButton r = new RadioButton(getContext());
                        r.setTag(R.id.radio_group, rg);
                        r.setTag(R.id.answer_option_field, qo);
                        r.setText(qo.getLabel());
                        r.setId(i);
                        r.setVisibility(View.VISIBLE);
                        //check if item registered
                        if (checked.containsKey(position)) {
                            Answer a = (Answer) checked.get(position);
                            r.setChecked((i == a.index));
                        }
                        //add radio button to group
                        rg.addView(r, j, new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                        //there is comment
                        if (qo.getRequireComment()) {
                            //create edit
                            EditText te = new EditText(getContext());
                            te.setBackgroundResource(R.drawable.blue_bottom_border);
                            te.setTextColor(R.color.black);
                            te.setTag(R.id.radio_group, rg);
                            te.addTextChangedListener(new GenericTextWatcher(te) {
                                @Override
                                public void afterTextChanged(Editable s) {
                                    RadioGroup gr = (RadioGroup) this.view.getTag(R.id.radio_group);
                                    int position = (Integer) gr.getTag(R.id.list_position);
                                    Answer a = (checked.containsKey(position)) ? checked.get(position) : (new Answer());
                                    a.comment = s.toString();
                                    checked.put(position, a);
                                }
                            });
                            if (checked.containsKey(position)) {
                                Answer a = (Answer) checked.get(position);
                                te.setText(a.comment);
                            }
                            //add edit to radio group
                            rg.addView(te, (++j), new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                            //if checked show edit text
                            te.setVisibility((r.isChecked()) ? View.VISIBLE : View.GONE);
                            //saving edit text to radion button
                            r.setTag(R.id.answer_comment_field, te);
                        }
                        //on radio button click
                        r.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                RadioGroup gr = (RadioGroup) buttonView.getTag(R.id.radio_group);
                                QuestionsOption qo = (QuestionsOption) buttonView.getTag(R.id.answer_option_field);
                                EditText te = (EditText) buttonView.getTag(R.id.answer_comment_field);

                                if (isChecked) {
                                    int pos = (Integer) gr.getTag(R.id.list_position);
                                    Answer a = (checked.containsKey(pos)) ? checked.get(pos) : (new Answer());
                                    a.index = buttonView.getId();
                                    a.question_id = qo.getQuestionId();
                                    a.picked_option_id = qo.getId();
                                    checked.put(pos, a);
                                }
                                if (te != null) {
                                    te.setVisibility((isChecked) ? View.VISIBLE : View.GONE);
                                }
                            }
                        });
                        j++;
                    }
                }
        }
        return v;
    }
}
