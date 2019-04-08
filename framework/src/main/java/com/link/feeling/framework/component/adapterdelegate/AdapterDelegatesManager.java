package com.link.feeling.framework.component.adapterdelegate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.link.feeling.framework.component.adapterdelegate.adapter.EmptyAdapterDelegate;
import com.link.feeling.framework.utils.data.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2019/1/8  15:20
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class AdapterDelegatesManager<T> {
        /**
         * Used internally for {@link #onBindViewHolder(Object, int, RecyclerView.ViewHolder)} as empty
         * payload parameter
         */
        private static final List<Object> PAYLOADS_EMPTY_LIST = Collections.emptyList();

        /**
         * Map for ViewType to AdapterDelegate
         */
        protected List<AdapterDelegate<T>> delegates = new ArrayList<>();

        private TypeIndex typeIndex = new TypeIndex();
        /**
         * Adds an {@link AdapterDelegate}.
         * <b>This method automatically assign internally the view type integer by using the next
         * unused</b>
         * allowReplacingDelegate = false as parameter.
         *
         * @param delegate the delegate to add
         * @return self
         * @throws NullPointerException if passed delegate is null
         */
        public AdapterDelegatesManager<T> addDelegate(@NonNull AdapterDelegate<T> delegate) {
        // algorithm could be improved since there could be holes,
        // but it's very unlikely that we reach Integer.MAX_VALUE and run out of unused indexes
        if (!delegates.contains(delegate))
            delegates.add(delegate);
        return this;
    }

        /**
         * Removes a previously registered delegate if and only if the passed delegate is registered
         * (checks the reference of the object). This will not remove any other delegate for the same
         * viewType (if there is any).
         *
         * @param delegate The delegate to remove
         * @return self
         */
        public AdapterDelegatesManager<T> removeDelegate(@NonNull AdapterDelegate<T> delegate) {
        if (delegate == null) {
            throw new NullPointerException("AdapterDelegate is null");
        }
        delegates.remove(delegate);
        return this;
    }

        /**
         * Removes the adapterDelegate for the given view types.
         *
         * @param viewType The Viewtype
         * @return self
         */
        public AdapterDelegatesManager<T> removeDelegate(int viewType) {
        delegates.remove(viewType);
        return this;
    }

        /**
         * This method must be called in {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
         *
         * @param parent   the parent
         * @param viewType the view type
         * @return The new created ViewHolder
         * @throws NullPointerException if no AdapterDelegate has been registered for ViewHolders
         *                              viewType
         */
        @NonNull
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterDelegate<T> delegate = getDelegateForViewType(viewType);
        if (delegate == null) {
            throw new NullPointerException("No AdapterDelegate added for ViewType " + viewType);
        }

        RecyclerView.ViewHolder vh = delegate.onCreateViewHolder(parent, viewType);
        if (vh == null) {
            throw new NullPointerException("ViewHolder returned from AdapterDelegate "
                    + delegate
                    + " for ViewType ="
                    + viewType
                    + " is null!");
        }
        return vh;
    }

        /**
         * Must be called from{@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)}
         * @param items      Adapter's data source
         * @param position   the position in data source
         * @param viewHolder the ViewHolder to bind
         * @param payloads   A non-null list of merged payloads. Can be empty list if requires full update.
         * @throws NullPointerException if no AdapterDelegate has been registered for ViewHolders
         *                              viewType
         */
        public void onBindViewHolder(@NonNull T items, int position,
        @NonNull RecyclerView.ViewHolder viewHolder, List payloads) {

        AdapterDelegate<T> delegate = getDelegateForViewType(viewHolder.getItemViewType());
        if (delegate == null) {
            throw new NullPointerException("No delegate found for item at position = "
                    + position
                    + " for viewType = "
                    + viewHolder.getItemViewType());
        }
        delegate.onBindViewHolder(items, position, viewHolder,
                payloads != null ? payloads : PAYLOADS_EMPTY_LIST);
    }

        /**
         * Must be called from {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)}
         * @param items      Adapter's data source
         * @param position   the position in data source
         * @param viewHolder the ViewHolder to bind
         * @throws NullPointerException if no AdapterDelegate has been registered for ViewHolders
         *                              viewType
         */
        public void onBindViewHolder(@NonNull T items, int position,
        @NonNull RecyclerView.ViewHolder viewHolder) {
        onBindViewHolder(items, position, viewHolder, PAYLOADS_EMPTY_LIST);
    }

        /**
         * Must be called from {@link RecyclerView.Adapter#onViewRecycled(RecyclerView.ViewHolder)}
         * @param viewHolder The ViewHolder for the view being recycled
         */
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder viewHolder) {
        AdapterDelegate<T> delegate = getDelegateForViewType(viewHolder.getItemViewType());
        if (delegate == null) {
            throw new NullPointerException("No delegate found for "
                    + viewHolder
                    + " for item at position = "
                    + viewHolder.getAdapterPosition()
                    + " for viewType = "
                    + viewHolder.getItemViewType());
        }
        delegate.onViewRecycled(viewHolder);
    }

        /**
         * Must be called from {@link RecyclerView.Adapter#onFailedToRecycleView(RecyclerView.ViewHolder)}
         * @param viewHolder The ViewHolder containing the View that could not be recycled due to its
         *                   transient state.
         * @return True if the View should be recycled, false otherwise. Note that if this method
         * returns <code>true</code>, RecyclerView <em>will ignore</em> the transient state of
         * the View and recycle it regardless. If this method returns <code>false</code>,
         * RecyclerView will check the View's transient state again before giving a final decision.
         * Default implementation returns false.
         */
        public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder viewHolder) {
        AdapterDelegate<T> delegate = getDelegateForViewType(viewHolder.getItemViewType());
        if (delegate == null) {
            throw new NullPointerException("No delegate found for "
                    + viewHolder
                    + " for item at position = "
                    + viewHolder.getAdapterPosition()
                    + " for viewType = "
                    + viewHolder.getItemViewType());
        }
        return delegate.onFailedToRecycleView(viewHolder);
    }

        /**
         * Must be called from {@link RecyclerView.Adapter#onViewAttachedToWindow(RecyclerView.ViewHolder)}
         * @param viewHolder Holder of the view being attached
         */
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        AdapterDelegate<T> delegate = getDelegateForViewType(viewHolder.getItemViewType());
        if (delegate == null) {
            throw new NullPointerException("No delegate found for "
                    + viewHolder
                    + " for item at position = "
                    + viewHolder.getAdapterPosition()
                    + " for viewType = "
                    + viewHolder.getItemViewType());
        }
        delegate.onViewAttachedToWindow(viewHolder);
    }

        /**
         * Must be called from {@link RecyclerView.Adapter#onViewDetachedFromWindow(RecyclerView.ViewHolder)}
         * @param viewHolder Holder of the view being attached
         */
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
        AdapterDelegate<T> delegate = getDelegateForViewType(viewHolder.getItemViewType());
        if (delegate == null) {
            throw new NullPointerException("No delegate found for "
                    + viewHolder
                    + " for item at position = "
                    + viewHolder.getAdapterPosition()
                    + " for viewType = "
                    + viewHolder.getItemViewType());
        }
        delegate.onViewDetachedFromWindow(viewHolder);
    }

        /**
         * Get the {@link AdapterDelegate} associated with the given view type integer
         *
         * @param viewType The view type integer we want to retrieve the associated
         *                 delegate for.
         * @return The {@link AdapterDelegate} associated with the view type param if it exists,
         * the fallback delegate otherwise if it is set or returns <code>null</code> if no delegate is
         * associated to this viewType (and no fallback has been set).
         */
        @Nullable
        AdapterDelegate<T> getDelegateForViewType(int viewType) {
        int index = typeIndex.getTypeIndex(viewType);
        if (index != -1) {
            return delegates.get(index);
        }
        return new EmptyAdapterDelegate();
    }

        /**
         * 获取默认 position
         * @param items
         * @param position, 没有任何子类处理此Type，如果type为负数，则Type值为负数最大值的position的偏移量（-Integer.max + position）
         *                  此处是为了保证每个Delegate持有一种type类型，且不会冲突
         */
        public int getItemViewType(T items, int position){
        for (int index = 0; index < delegates.size(); index++) {
            AdapterDelegate<T> delegate = delegates.get(index);

            int type = delegate.getItemViewType(items, position);

            // Delegate通过ViewType匹配，找到处理类
            if (type != TypeIndex.TYPE_NOLL) {
                typeIndex.putType(index, type);
                return type;
            }
            // Delegate通过ViewType匹配，未找到处理类，尝试通过类型匹配
            if (delegate.isForViewType(items, position)) {
                return typeIndex.putType(index);
            }
        }
        if (items instanceof List && ((List) items).size() > position){
            L.e("AdapterDelegatesManager", " invalid viewType for " + position + " item" + ((List)items).get(position));
        }
        return TypeIndex.TYPE_NOLL;
    }
}
