<template>
  <span class="unify-list-item-label">
    <strong class="d-block d-md-inline-block g-color-gray-dark-v2 g-pr-10">
      <label :for="this.for">
        <slot></slot>
      </label>
    </strong>
  </span>
</template>

<script>
  export default {
    name: "unify-list-item-label",

    props: ['for']
  }
</script>
