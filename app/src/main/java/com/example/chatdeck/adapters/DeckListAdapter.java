package com.example.chatdeck.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.example.chatdeck.ChatActivity;
import com.example.chatdeck.R;
import com.example.chatdeck.utils.mImageLoader;

import java.util.ArrayList;
import java.util.List;
// Chun Kit Jonathan Chan

public class DeckListAdapter extends RecyclerView.Adapter<DeckListAdapter.DeckListViewHolder> {

    // region Member elements
    private static final String TAG = DeckListAdapter.class.getSimpleName();
    private List<Group> decks;
    private Context context;
    // endregion

    // region Constructor for DeckListAdapter
    public DeckListAdapter(List<Group> decks, Context context) {
        this.decks = decks;
        this.context = context;
    }
    // end region

    @NonNull
    @Override
    public DeckListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.deck_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        DeckListViewHolder viewHolder = new DeckListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeckListViewHolder holder, int position) {
        holder.bind(decks.get(position));
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    class DeckListViewHolder extends RecyclerView.ViewHolder{

        // region Field Members
        TextView tv_deckName;
        TextView tv_deckMemberName;
        TextView tv_deckMemberTitle;
        TextView tv_deckDescription;
        TextView tv_deckDescriptionTitle;
        ImageView iv_deckIcon;
        LinearLayout deckListContainer;
        ConstraintLayout expandable;
        ImageButton dropDownBtn;

        GroupMembersRequest groupMembersRequest;
        int groupMembersRequestLimit = 10;
        // end region

        // region Constructor for DeskListViewHolder
        public DeckListViewHolder(@NonNull View itemView) {
            super(itemView);

            // region Get View elements
            tv_deckName = itemView.findViewById(R.id.tv_deckName);
            tv_deckMemberName = itemView.findViewById(R.id.tv_deckMemberList);
            tv_deckDescription = itemView.findViewById(R.id.tv_deckDescription);
            tv_deckDescriptionTitle = itemView.findViewById(R.id.tv_deckDescriptionTitle);
            tv_deckMemberTitle = itemView.findViewById(R.id.tv_deckMemberTitle);
            iv_deckIcon = itemView.findViewById(R.id.iv_deckIcon);
            deckListContainer = itemView.findViewById(R.id.deckListContainer);
            expandable = itemView.findViewById(R.id.rv_expandable);
            dropDownBtn = itemView.findViewById(R.id.btn_dropDown);
            // end region

            dropDownBtn.setOnClickListener(v -> {
                if(expandable.getVisibility() == View.GONE){
                    expandable.setVisibility(View.VISIBLE);
                }
                else if (expandable.getVisibility() == View.VISIBLE){
                    expandable.setVisibility(View.GONE);
                }
            });
        }
        // endregion

        void bind(Group decks) {
            // Deck icon
            if (decks.getIcon() != null){
            mImageLoader updateIcon = new mImageLoader(decks.getIcon(), iv_deckIcon);
            updateIcon.execute();
            }
            // Deck name
            tv_deckName.setText(decks.getName());
            // Deck member list
            getDeckMemberList(decks.getGuid());
            // Deck description
            if (decks.getDescription() != null)
            {
                tv_deckDescription.setText(decks.getDescription());
                tv_deckDescription.setVisibility(View.VISIBLE);
                tv_deckDescriptionTitle.setVisibility(View.VISIBLE);
                dropDownBtn.setVisibility(View.VISIBLE);
            }

            // direct user to ChatActivity when user clicked on recyclerView's item
            deckListContainer.setOnClickListener(view -> ChatActivity.start(context, decks.getGuid()));
        }

        // region Method to get all members in deck (group)
        private void getDeckMemberList(String deckId) {
            groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(deckId).setLimit(groupMembersRequestLimit).build();
            groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>(){
                @Override
                public void onSuccess(List<GroupMember> list) {
                    Log.d(TAG, "Deck Member list fetched successfully: " + list.size());
                    ArrayList<String> deckMemberNameList = new ArrayList<>();
                    for (GroupMember deckMember : list) {
                        // save deckMember's name from list to an ArrayList for display
                        deckMemberNameList.add(deckMember.getName());
                    }

                    // region Display deckMember's name with String manipulation
                    if (deckMemberNameList != null)
                    {
                        tv_deckMemberName.setText(deckMemberNameList.toString()
                                .replace("[", " ")
                                .replace("]", "")
                                .replace(",", "\n"));


                        tv_deckMemberName.setVisibility(View.VISIBLE);
                        tv_deckMemberTitle.setVisibility(View.VISIBLE);
                        dropDownBtn.setVisibility(View.VISIBLE);
                    }
                    // end region
                }
                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "Deck Member list fetching failed with exception: " + e.getMessage());
                }
            });
        }
        // end region
    }
}


